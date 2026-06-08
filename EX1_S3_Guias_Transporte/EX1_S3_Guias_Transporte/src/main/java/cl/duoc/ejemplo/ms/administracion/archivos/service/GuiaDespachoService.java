package cl.duoc.ejemplo.ms.administracion.archivos.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.GuiaDespachoRequest;
import cl.duoc.ejemplo.ms.administracion.archivos.dto.GuiaDespachoResponse;
import cl.duoc.ejemplo.ms.administracion.archivos.dto.S3ObjectDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuiaDespachoService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final AwsS3Service awsS3Service;
    private final EfsService efsService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public GuiaDespachoResponse crearGuia(GuiaDespachoRequest request) throws Exception {
        validarRequest(request);

        String fecha = LocalDate.now().format(FORMATTER);
        String transportista = limpiar(request.getTransportista());
        String idGuia = "guia-" + UUID.randomUUID().toString().substring(0, 8);
        String key = construirKey(fecha, transportista, idGuia);

        byte[] pdf = generarPdfSimple(idGuia, request, fecha);
        File archivoEfs = efsService.saveBytesToEfs(key, pdf);
        awsS3Service.uploadBytes(bucket, key, pdf, "application/pdf");

        return new GuiaDespachoResponse(idGuia, bucket, key, archivoEfs.getAbsolutePath(),
                "Guía creada en EFS y subida automáticamente a S3");
    }

    public GuiaDespachoResponse subirGuiaGenerada(String fecha, String transportista, String idGuia, MultipartFile file)
            throws Exception {
        String key = construirKey(fecha, limpiar(transportista), limpiar(idGuia));
        File archivoEfs = efsService.saveToEfs(key, file);
        awsS3Service.upload(bucket, key, file);
        return new GuiaDespachoResponse(idGuia, bucket, key, archivoEfs.getAbsolutePath(), "Guía subida a S3");
    }

    public GuiaDespachoResponse actualizarGuia(String fecha, String transportista, String idGuia, MultipartFile file)
            throws Exception {
        String key = construirKey(fecha, limpiar(transportista), limpiar(idGuia));
        File archivoEfs = efsService.saveToEfs(key, file);
        awsS3Service.upload(bucket, key, file);
        return new GuiaDespachoResponse(idGuia, bucket, key, archivoEfs.getAbsolutePath(), "Guía actualizada en EFS y S3");
    }

    public byte[] descargarGuia(String fecha, String transportista, String idGuia, String usuarioSolicitante) {
        validarPermiso(transportista, usuarioSolicitante);
        String key = construirKey(fecha, limpiar(transportista), limpiar(idGuia));
        return awsS3Service.downloadAsBytes(bucket, key);
    }

    public void eliminarGuia(String fecha, String transportista, String idGuia) {
        String key = construirKey(fecha, limpiar(transportista), limpiar(idGuia));
        awsS3Service.deleteObject(bucket, key);
        efsService.deleteFromEfs(key);
    }

    public List<S3ObjectDto> consultarHistorial(String fecha, String transportista) {
        String prefix = fecha + "/" + limpiar(transportista) + "/";
        return awsS3Service.listObjectsByPrefix(bucket, prefix);
    }

    public String construirKey(String fecha, String transportista, String idGuia) {
        String nombreGuia = idGuia.endsWith(".pdf") ? idGuia : idGuia + ".pdf";
        return fecha + "/" + transportista + "/" + nombreGuia;
    }

    private void validarRequest(GuiaDespachoRequest request) {
        if (request == null || esVacio(request.getTransportista()) || esVacio(request.getNumeroPedido())
                || esVacio(request.getDestinatario()) || esVacio(request.getDireccionDestino())) {
            throw new IllegalArgumentException("Debe informar número de pedido, transportista, destinatario y destino");
        }
    }

    private void validarPermiso(String transportista, String usuarioSolicitante) {
        if (esVacio(usuarioSolicitante) || !limpiar(usuarioSolicitante).equals(limpiar(transportista))) {
            throw new SecurityException("El usuario solicitante no tiene permisos para descargar esta guía");
        }
    }

    private boolean esVacio(String value) {
        return value == null || value.isBlank();
    }

    private String limpiar(String value) {
        if (value == null) {
            return "sin-dato";
        }
        String normalizado = Normalizer.normalize(value.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalizado.replaceAll("[^a-zA-Z0-9._-]", "-");
    }

    private byte[] generarPdfSimple(String idGuia, GuiaDespachoRequest request, String fecha) {
        String texto = "Guía de Despacho " + idGuia + "\\n" + "Fecha: " + fecha + "\\n" + "Pedido: "
                + request.getNumeroPedido() + "\\n" + "Transportista: " + request.getTransportista() + "\\n"
                + "Destinatario: " + request.getDestinatario() + "\\n" + "Destino: " + request.getDireccionDestino()
                + "\\n" + "Carga: " + request.getDescripcionCarga();

        String pdf = "%PDF-1.4\n" + "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n"
                + "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n"
                + "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >> endobj\n"
                + "4 0 obj << /Length " + (texto.length() + 80) + " >> stream\n"
                + "BT /F1 12 Tf 50 740 Td (" + texto.replace("\\n", ") Tj T* (") + ") Tj ET\n"
                + "endstream endobj\n" + "xref\n0 5\n0000000000 65535 f \n" + "trailer << /Root 1 0 R >>\n"
                + "%%EOF";
        return pdf.getBytes(StandardCharsets.UTF_8);
    }
}
