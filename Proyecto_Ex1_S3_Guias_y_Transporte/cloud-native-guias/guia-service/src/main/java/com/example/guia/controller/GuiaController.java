package com.example.guia.controller;
import com.example.guia.dto.GuiaRequest;
import com.example.guia.model.Guia;
import com.example.guia.service.GuiaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; import java.util.List;
@RestController
@RequestMapping("/api/guias")
public class GuiaController {
 private final GuiaService service; public GuiaController(GuiaService service){this.service=service;}
 @PostMapping public ResponseEntity<Guia> crear(@RequestBody GuiaRequest request) throws Exception { return ResponseEntity.ok(service.crear(request)); }
 @PostMapping("/{id}/upload") public ResponseEntity<Guia> subir(@PathVariable Long id) throws Exception { return ResponseEntity.ok(service.subir(id)); }
 @GetMapping("/{id}/download") public ResponseEntity<byte[]> descargar(@PathVariable Long id, @RequestHeader("X-Transportista") String solicitante){ byte[] data=service.descargar(id, solicitante); return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=guia-"+id+".txt").contentType(MediaType.TEXT_PLAIN).body(data); }
 @PutMapping("/{id}") public ResponseEntity<Guia> actualizar(@PathVariable Long id, @RequestBody GuiaRequest request) throws Exception { return ResponseEntity.ok(service.actualizar(id, request)); }
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestHeader("X-Transportista") String solicitante){ service.eliminar(id, solicitante); return ResponseEntity.noContent().build(); }
 @GetMapping public List<Guia> consultar(@RequestParam String transportista, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fecha){ return service.consultar(transportista, fecha); }
 @ExceptionHandler(SecurityException.class) public ResponseEntity<String> sinPermiso(SecurityException e){ return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); }
}
