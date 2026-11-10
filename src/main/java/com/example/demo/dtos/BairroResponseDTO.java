package com.example.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BairroResponseDTO {
    private Integer id;
    private String nome;
    private String cidade;
    private String estado;
    private String cepInicial;
    private String cepFinal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
