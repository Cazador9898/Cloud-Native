package com.example.proyecto.service;

import com.example.proyecto.dto.GuiaRequest;
import com.example.proyecto.model.Guia;
import com.example.proyecto.repository.GuiaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GuiaService {
    private final GuiaRepository guiaRepository;
    private final S3Client s3Client;

    @Value("${app.efs.path}")
    private String efsPath;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.enabled:false}")
    private boolean s3Enabled;

    public GuiaService(GuiaRepository guiaRepository, S3Client s3Client) {
        this.guiaRepository = guiaRepository;
        this.s3Client = s3Client;
    }

    public Guia crearGuia(GuiaRequest request) throws IOException {
        Guia guia = new Guia();
        guia.setNumeroGuia("GUIA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        guia.setTransportista(request.getTransportista());
        guia.setDestinatario(request.getDestinatario());
        guia.setDireccionDestino(request.getDireccionDestino());
        guia.setDetallePedido(request.getDetallePedido());
        guia.setFecha(LocalDate.now());
        guia.setEstado("GENERADA_EN_EFS");

        String fileName = guia.getNumeroGuia() + ".txt";
        Path carpeta = Path.of(efsPath, guia.getFecha().toString(), limpiarNombre(guia.getTransportista()));
        Files.createDirectories(carpeta);
        Path archivo = carpeta.resolve(fileName);

        String contenido = "GUIA DE DESPACHO\n"
                + "Numero: " + guia.getNumeroGuia() + "\n"
                + "Fecha: " + guia.getFecha() + "\n"
                + "Transportista: " + guia.getTransportista() + "\n"
                + "Destinatario: " + guia.getDestinatario() + "\n"
                + "Direccion: " + guia.getDireccionDestino() + "\n"
                + "Detalle: " + guia.getDetallePedido() + "\n";

        Files.writeString(archivo, contenido);
        guia.setEfsPath(archivo.toString());
        return guiaRepository.save(guia);
    }

    public Guia subirAS3(Long id) {
        Guia guia = buscar(id);
        Path archivo = Path.of(guia.getEfsPath());
        String key = guia.getFecha() + "/" + limpiarNombre(guia.getTransportista()) + "/" + archivo.getFileName();

        if (s3Enabled) {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key).build(),
                    RequestBody.fromFile(archivo)
            );
        }

        guia.setS3Key(key);
        guia.setEstado(s3Enabled ? "SUBIDA_A_S3" : "SIMULADA_EN_S3_LOCAL");
        return guiaRepository.save(guia);
    }

    public Resource descargar(Long id) throws IOException {
        Guia guia = buscar(id);

        if (s3Enabled && guia.getS3Key() != null) {
            Path temporal = Path.of(efsPath, "descargas", guia.getNumeroGuia() + ".txt");
            Files.createDirectories(temporal.getParent());
            s3Client.getObject(
                    GetObjectRequest.builder().bucket(bucket).key(guia.getS3Key()).build(),
                    temporal
            );
            return new FileSystemResource(temporal);
        }

        return new FileSystemResource(Path.of(guia.getEfsPath()));
    }

    public Guia actualizar(Long id, GuiaRequest request) throws IOException {
        Guia guia = buscar(id);
        guia.setTransportista(request.getTransportista());
        guia.setDestinatario(request.getDestinatario());
        guia.setDireccionDestino(request.getDireccionDestino());
        guia.setDetallePedido(request.getDetallePedido());
        guia.setEstado("ACTUALIZADA");

        Path archivo = Path.of(guia.getEfsPath());
        String contenido = "GUIA DE DESPACHO ACTUALIZADA\n"
                + "Numero: " + guia.getNumeroGuia() + "\n"
                + "Fecha: " + guia.getFecha() + "\n"
                + "Transportista: " + guia.getTransportista() + "\n"
                + "Destinatario: " + guia.getDestinatario() + "\n"
                + "Direccion: " + guia.getDireccionDestino() + "\n"
                + "Detalle: " + guia.getDetallePedido() + "\n";
        Files.writeString(archivo, contenido);

        if (guia.getS3Key() != null) {
            subirAS3(id);
        }
        return guiaRepository.save(guia);
    }

    public void eliminar(Long id) throws IOException {
        Guia guia = buscar(id);
        if (guia.getEfsPath() != null) {
            Files.deleteIfExists(Path.of(guia.getEfsPath()));
        }
        guiaRepository.delete(guia);
    }

    public List<Guia> consultar(String transportista, LocalDate fecha) {
        return guiaRepository.findByTransportistaIgnoreCaseAndFecha(transportista, fecha);
    }

    public List<Guia> listar() {
        return guiaRepository.findAll();
    }

    private Guia buscar(Long id) {
        return guiaRepository.findById(id).orElseThrow(() -> new RuntimeException("Guía no encontrada: " + id));
    }

    private String limpiarNombre(String texto) {
        if (texto == null || texto.isBlank()) return "sin-transportista";
        return texto.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
