package com.example.transportista.controller;
import com.example.transportista.model.Transportista;
import com.example.transportista.repository.TransportistaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {
  private final TransportistaRepository repo;
  public TransportistaController(TransportistaRepository repo){this.repo=repo;}
  @PostMapping public Transportista crear(@RequestBody Transportista t){return repo.save(t);} 
  @GetMapping public List<Transportista> listar(){return repo.findAll();}
  @GetMapping("/{codigo}") public ResponseEntity<Transportista> buscar(@PathVariable String codigo){return repo.findById(codigo).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
}
