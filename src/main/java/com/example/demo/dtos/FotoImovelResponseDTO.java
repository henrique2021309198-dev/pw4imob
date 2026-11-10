package com.example.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FotoImovelResponseDTO {
    private Integer id;
    private ImovelResponseDTO imovel;
    private String nomeArquivo;
    private String caminho;
    private Boolean capa;
    private Integer ordem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
