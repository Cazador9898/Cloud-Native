package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.GuiaDespachoRequest;
import cl.duoc.ejemplo.ms.administracion.archivos.dto.GuiaDespachoResponse;
import cl.duoc.ejemplo.ms.administracion.archivos.dto.S3ObjectDto;
import cl.duoc.ejemplo.ms.administracion.archivos.service.GuiaDespachoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaDespachoController {

    private final GuiaDespachoService guiaDespachoService;

    @PostMapping
    public ResponseEntity<GuiaDespachoResponse> crearGuia(@RequestBody GuiaDespachoRequest request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(guiaDespachoService.crearGuia(request));
    }

    @PostMapping("/upload")
    public ResponseEntity<GuiaDespachoResponse> subirGuia(@RequestParam String fecha,
            @RequestParam String transportista, @RequestParam String idGuia, @RequestParam("file") MultipartFile file)
            throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guiaDespachoService.subirGuiaGenerada(fecha, transportista, idGuia, file));
    }

    @GetMapping("/{fecha}/{transportista}/{idGuia}")
    public ResponseEntity<byte[]> descargarGuia(@PathVariable String fecha, @PathVariable String transportista,
            @PathVariable String idGuia, @RequestParam String usuarioSolicitante) {
        byte[] bytes = guiaDespachoService.descargarGuia(fecha, transportista, idGuia, usuarioSolicitante);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + idGuia + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PutMapping("/{fecha}/{transportista}/{idGuia}")
    public ResponseEntity<GuiaDespachoResponse> actualizarGuia(@PathVariable String fecha,
            @PathVariable String transportista, @PathVariable String idGuia, @RequestParam("file") MultipartFile file)
            throws Exception {
        return ResponseEntity.ok(guiaDespachoService.actualizarGuia(fecha, transportista, idGuia, file));
    }

    @DeleteMapping("/{fecha}/{transportista}/{idGuia}")
    public ResponseEntity<Void> eliminarGuia(@PathVariable String fecha, @PathVariable String transportista,
            @PathVariable String idGuia) {
        guiaDespachoService.eliminarGuia(fecha, transportista, idGuia);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<S3ObjectDto>> consultarHistorial(@RequestParam String fecha,
            @RequestParam String transportista) {
        return ResponseEntity.ok(guiaDespachoService.consultarHistorial(fecha, transportista));
    }
}
