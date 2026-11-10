package com.example.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private String tipo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
