package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiaDespachoRequest {

    private String numeroPedido;
    private String transportista;
    private String usuarioAutorizado;
    private String destinatario;
    private String direccionDestino;
    private String descripcionCarga;
}
