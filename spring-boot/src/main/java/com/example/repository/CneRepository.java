package com.example.repository;

import com.example.model.Cne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CneRepository extends JpaRepository<Cne, Long> {
}
