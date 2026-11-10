package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ImovelResponseDTO {
    private Integer id;
    private String titulo;
    private String descricao;
    private BigDecimal precoVenda;
    private BigDecimal precoAluguel;
    private String finalidade;
    private String status;
    private Integer dormitorios;
    private Integer banheiros;
    private Integer garagem;
    private BigDecimal areaTotal;
    private BigDecimal areaConstruida;
    private String endereco;
    private String numero;
    private String complemento;
    private String cep;
    private String caracteristicas;
    private Boolean destaque;
    private UserResponseDTO usuario;
    private TipoImovelResponseDTO tipoImovel;
    private BairroResponseDTO bairro;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
