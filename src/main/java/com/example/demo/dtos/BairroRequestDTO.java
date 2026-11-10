package com.example.demo.dtos;

import lombok.Data;

@Data
public class BairroRequestDTO {
    private String nome;
    private String cidade;
    private String estado;
    private String cepInicial;
    private String cepFinal;
}
