package cl.duoc.ejemplo.ms.administracion.archivos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.TransportistaResponse;

@Service
public class TransportistaService {

    private final List<TransportistaResponse> transportistas = List.of(
            new TransportistaResponse("transportistaX", "Transportes Express X", "76.123.456-7", "Carlos Soto", "ABCD-12"),
            new TransportistaResponse("transportistaNorte", "Transportes Norte Ltda.", "77.234.567-8", "María Rojas", "EFGH-34"),
            new TransportistaResponse("transportistaSur", "Logística Sur SpA", "78.345.678-9", "Pedro Muñoz", "IJKL-56")
    );

    public List<TransportistaResponse> listarTransportistas() {
        return transportistas;
    }

    public Optional<TransportistaResponse> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return transportistas.stream()
                .filter(t -> t.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }
}
