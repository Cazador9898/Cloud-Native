package com.example.guia.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class Guia {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String numero; private String transportista; private LocalDate fecha;
 private String destinatario; private String direccionDestino; @Column(length=2000) private String detalle;
 private String efsPath; private String s3Key;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getNumero(){return numero;} public void setNumero(String numero){this.numero=numero;}
 public String getTransportista(){return transportista;} public void setTransportista(String transportista){this.transportista=transportista;}
 public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate fecha){this.fecha=fecha;}
 public String getDestinatario(){return destinatario;} public void setDestinatario(String destinatario){this.destinatario=destinatario;}
 public String getDireccionDestino(){return direccionDestino;} public void setDireccionDestino(String direccionDestino){this.direccionDestino=direccionDestino;}
 public String getDetalle(){return detalle;} public void setDetalle(String detalle){this.detalle=detalle;}
 public String getEfsPath(){return efsPath;} public void setEfsPath(String efsPath){this.efsPath=efsPath;}
 public String getS3Key(){return s3Key;} public void setS3Key(String s3Key){this.s3Key=s3Key;}
}
