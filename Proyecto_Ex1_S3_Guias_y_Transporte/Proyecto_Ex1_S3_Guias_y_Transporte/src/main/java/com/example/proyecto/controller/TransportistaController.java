package com.example.proyecto.controller;

import com.example.proyecto.model.Transportista;
import com.example.proyecto.repository.TransportistaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {
    private final TransportistaRepository repository;

    public TransportistaController(TransportistaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Transportista crear(@RequestBody Transportista transportista) {
        return repository.save(transportista);
    }

    @GetMapping
    public List<Transportista> listar() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public Transportista actualizar(@PathVariable Long id, @RequestBody Transportista datos) {
        Transportista t = repository.findById(id).orElseThrow(() -> new RuntimeException("Transportista no encontrado"));
        t.setNombre(datos.getNombre());
        t.setRut(datos.getRut());
        t.setEmail(datos.getEmail());
        return repository.save(t);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
