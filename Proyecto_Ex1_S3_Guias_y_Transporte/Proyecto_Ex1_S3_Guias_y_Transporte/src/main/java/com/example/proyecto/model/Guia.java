package com.example.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Guia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroGuia;
    private String transportista;
    private String destinatario;
    private String direccionDestino;
    private String detallePedido;
    private LocalDate fecha;
    private String estado;
    private String efsPath;
    private String s3Key;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public String getDetallePedido() { return detallePedido; }
    public void setDetallePedido(String detallePedido) { this.detallePedido = detallePedido; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEfsPath() { return efsPath; }
    public void setEfsPath(String efsPath) { this.efsPath = efsPath; }
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
}
