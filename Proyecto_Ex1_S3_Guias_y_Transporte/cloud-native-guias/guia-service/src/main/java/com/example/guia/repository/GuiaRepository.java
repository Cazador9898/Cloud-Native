package com.example.guia.repository;
import com.example.guia.model.Guia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;
public interface GuiaRepository extends JpaRepository<Guia, Long> {
 List<Guia> findByTransportistaAndFecha(String transportista, LocalDate fecha);
}
