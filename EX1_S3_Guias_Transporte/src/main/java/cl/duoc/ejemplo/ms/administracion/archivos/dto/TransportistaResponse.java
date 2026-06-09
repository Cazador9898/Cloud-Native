package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportistaResponse {

    private String codigo;
    private String nombre;
    private String rut;
    private String conductor;
    private String patente;
}
