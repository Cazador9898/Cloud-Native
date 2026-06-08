package com.example.guia.service;
import com.example.guia.dto.GuiaRequest;
import com.example.guia.model.Guia;
import com.example.guia.repository.GuiaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
public class GuiaService {
 private final GuiaRepository repo; private final S3Client s3;
 @Value("${app.efs.path}") private String efsPath;
 @Value("${app.s3.bucket}") private String bucket;
 public GuiaService(GuiaRepository repo, S3Client s3){this.repo=repo;this.s3=s3;}
 public Guia crear(GuiaRequest r) throws Exception { Guia g = new Guia(); copiar(r,g); g.setFecha(LocalDate.now()); escribirEfs(g); subirS3(g); return repo.save(g); }
 public Guia actualizar(Long id, GuiaRequest r) throws Exception { Guia g = repo.findById(id).orElseThrow(); copiar(r,g); escribirEfs(g); subirS3(g); return repo.save(g); }
 public Guia subir(Long id) throws Exception { Guia g=repo.findById(id).orElseThrow(); subirS3(g); return repo.save(g); }
 public byte[] descargar(Long id, String solicitante) { Guia g=repo.findById(id).orElseThrow(); validar(g, solicitante); ResponseBytes<GetObjectResponse> obj=s3.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(g.getS3Key()).build()); return obj.asByteArray(); }
 public List<Guia> consultar(String transportista, LocalDate fecha){ return repo.findByTransportistaAndFecha(transportista, fecha); }
 public void eliminar(Long id, String solicitante){ Guia g=repo.findById(id).orElseThrow(); validar(g, solicitante); if(g.getS3Key()!=null){ s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(g.getS3Key()).build()); } repo.delete(g); }
 public void validar(Guia g, String solicitante){ if(!"ADMIN".equalsIgnoreCase(solicitante) && !g.getTransportista().equalsIgnoreCase(solicitante)) throw new SecurityException("No tiene permisos para esta guia"); }
 private void copiar(GuiaRequest r, Guia g){ g.setNumero(r.numero); g.setTransportista(r.transportista); g.setDestinatario(r.destinatario); g.setDireccionDestino(r.direccionDestino); g.setDetalle(r.detalle); }
 private void escribirEfs(Guia g) throws Exception { Path dir=Paths.get(efsPath, g.getFecha()==null?LocalDate.now().toString():g.getFecha().toString(), g.getTransportista()); Files.createDirectories(dir); String nombre=(g.getNumero()==null?"guia":g.getNumero())+".txt"; Path archivo=dir.resolve(nombre); String texto="GUIA DE DESPACHO\nNumero: "+g.getNumero()+"\nTransportista: "+g.getTransportista()+"\nFecha: "+(g.getFecha()==null?LocalDate.now():g.getFecha())+"\nDestinatario: "+g.getDestinatario()+"\nDireccion: "+g.getDireccionDestino()+"\nDetalle: "+g.getDetalle()+"\n"; Files.writeString(archivo,texto,StandardCharsets.UTF_8); g.setEfsPath(archivo.toString()); }
 private void subirS3(Guia g) throws Exception { LocalDate fecha=g.getFecha()==null?LocalDate.now():g.getFecha(); String key=fecha.format(DateTimeFormatter.BASIC_ISO_DATE)+"/"+g.getTransportista()+"/"+g.getNumero()+".txt"; s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType("text/plain").build(), RequestBody.fromFile(Paths.get(g.getEfsPath()))); g.setS3Key(key); }
}
