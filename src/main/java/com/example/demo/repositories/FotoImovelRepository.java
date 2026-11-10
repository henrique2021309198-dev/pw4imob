package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.FotoImovel;

public interface FotoImovelRepository extends JpaRepository<FotoImovel, Integer> {
    List<FotoImovel> findByImovelId(Integer imovelId);
}
