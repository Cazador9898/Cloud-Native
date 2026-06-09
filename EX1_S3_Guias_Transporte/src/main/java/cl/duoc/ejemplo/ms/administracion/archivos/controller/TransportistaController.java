package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.TransportistaResponse;
import cl.duoc.ejemplo.ms.administracion.archivos.service.TransportistaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;

    @GetMapping
    public ResponseEntity<List<TransportistaResponse>> listarTransportistas() {
        return ResponseEntity.ok(transportistaService.listarTransportistas());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<TransportistaResponse> buscarTransportista(@PathVariable String codigo) {
        return transportistaService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
