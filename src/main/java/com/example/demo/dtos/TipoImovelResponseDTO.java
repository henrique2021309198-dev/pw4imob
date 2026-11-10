package com.example.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TipoImovelResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
