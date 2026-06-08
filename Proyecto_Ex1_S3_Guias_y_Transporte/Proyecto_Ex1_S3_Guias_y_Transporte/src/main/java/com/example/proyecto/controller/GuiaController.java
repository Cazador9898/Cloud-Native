package com.example.proyecto.controller;

import com.example.proyecto.dto.GuiaRequest;
import com.example.proyecto.model.Guia;
import com.example.proyecto.service.GuiaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {
    private final GuiaService guiaService;

    @Value("${app.api.key}")
    private String apiKey;

    public GuiaController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @PostMapping
    public Guia crear(@RequestBody GuiaRequest request) throws IOException {
        return guiaService.crearGuia(request);
    }

    @PostMapping("/{id}/subir-s3")
    public Guia subirAS3(@PathVariable Long id) {
        return guiaService.subirAS3(id);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id,
                                              @RequestHeader(value = "X-API-KEY", required = false) String key) throws IOException {
        if (!apiKey.equals(key)) {
            return ResponseEntity.status(403).build();
        }
        Resource archivo = guiaService.descargar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia-" + id + ".txt")
                .body(archivo);
    }

    @PutMapping("/{id}")
    public Guia actualizar(@PathVariable Long id, @RequestBody GuiaRequest request) throws IOException {
        return guiaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) throws IOException {
        guiaService.eliminar(id);
    }

    @GetMapping("/consultar")
    public List<Guia> consultar(@RequestParam String transportista,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return guiaService.consultar(transportista, fecha);
    }

    @GetMapping
    public List<Guia> listar() {
        return guiaService.listar();
    }
}
