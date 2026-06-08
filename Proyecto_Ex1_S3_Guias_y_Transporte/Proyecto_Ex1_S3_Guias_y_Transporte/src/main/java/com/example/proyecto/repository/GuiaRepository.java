package com.example.proyecto.repository;

import com.example.proyecto.model.Guia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GuiaRepository extends JpaRepository<Guia, Long> {
    List<Guia> findByTransportistaIgnoreCaseAndFecha(String transportista, LocalDate fecha);
}
