package com.example.demo.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaberMaisRequestDTO {
    private String nome;
    private String telefone;
    private String email;
    private String mensagem;
}
