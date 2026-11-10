package com.example.demo.dtos;

import lombok.Data;

@Data
public class FotoImovelRequestDTO {
    private Integer imovelId;
    private String nomeArquivo;
    private String caminho;
    private Boolean capa;
    private Integer ordem;
}
