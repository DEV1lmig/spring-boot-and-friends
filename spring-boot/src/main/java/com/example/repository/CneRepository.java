package com.example.repository;

import com.example.model.Cne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CneRepository extends JpaRepository<Cne, Long> {
    Optional<Cne> findByCedula(String cedula);
}
