package com.example.demo.dtos;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String nome;
    private String email;
    private String senha;
    private String tipo;
}
