package com.example.repository;

import com.example.model.CneFotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CneFotosRepository extends JpaRepository<CneFotos, Long> {

}
