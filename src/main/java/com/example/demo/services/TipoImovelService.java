package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.TipoImovelRequestDTO;
import com.example.demo.dtos.TipoImovelResponseDTO;
import com.example.demo.models.TipoImovel;
import com.example.demo.repositories.TipoImovelRepository;

@Service
public class TipoImovelService {

    @Autowired
    private TipoImovelRepository tipoImovelRepository;

    public List<TipoImovelResponseDTO> findAll() {
        return tipoImovelRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Page<TipoImovelResponseDTO> findAll(Pageable pageable) {
        return tipoImovelRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    public TipoImovelResponseDTO findById(Integer id) {
        Optional<TipoImovel> tipo = tipoImovelRepository.findById(id);
        return tipo.map(this::toResponseDTO).orElse(null);
    }

    public TipoImovelResponseDTO create(TipoImovelRequestDTO dto) {
        TipoImovel tipo = new TipoImovel();
        tipo.setNome(dto.getNome());
        tipo.setDescricao(dto.getDescricao());
        tipo.setCreatedAt(LocalDateTime.now());
        tipo.setUpdatedAt(LocalDateTime.now());
        return toResponseDTO(tipoImovelRepository.save(tipo));
    }

    public TipoImovelResponseDTO update(Integer id, TipoImovelRequestDTO dto) {
        Optional<TipoImovel> optional = tipoImovelRepository.findById(id);
        if (optional.isPresent()) {
            TipoImovel tipo = optional.get();
            tipo.setNome(dto.getNome());
            tipo.setDescricao(dto.getDescricao());
            tipo.setUpdatedAt(LocalDateTime.now());
            return toResponseDTO(tipoImovelRepository.save(tipo));
        }
        return null;
    }

    public boolean delete(Integer id) {
        if (tipoImovelRepository.existsById(id)) {
            tipoImovelRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TipoImovelResponseDTO toResponseDTO(TipoImovel tipo) {
        TipoImovelResponseDTO dto = new TipoImovelResponseDTO();
        dto.setId(tipo.getId());
        dto.setNome(tipo.getNome());
        dto.setDescricao(tipo.getDescricao());
        dto.setCreatedAt(tipo.getCreatedAt());
        dto.setUpdatedAt(tipo.getUpdatedAt());
        return dto;
    }
}
