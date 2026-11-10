package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.BairroRequestDTO;
import com.example.demo.dtos.BairroResponseDTO;
import com.example.demo.models.Bairro;
import com.example.demo.repositories.BairroRepository;

@Service
public class BairroService {

    @Autowired
    private BairroRepository bairroRepository;

    public List<BairroResponseDTO> findAll() {
        return bairroRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Page<BairroResponseDTO> findAll(Pageable pageable) {
        return bairroRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    public BairroResponseDTO findById(Integer id) {
        Optional<Bairro> bairro = bairroRepository.findById(id);
        return bairro.map(this::toResponseDTO).orElse(null);
    }

    public BairroResponseDTO create(BairroRequestDTO dto) {
        Bairro bairro = new Bairro();
        bairro.setNome(dto.getNome());
        bairro.setCidade(dto.getCidade());
        bairro.setEstado(dto.getEstado());
        bairro.setCepInicial(dto.getCepInicial());
        bairro.setCepFinal(dto.getCepFinal());
        bairro.setCreatedAt(LocalDateTime.now());
        bairro.setUpdatedAt(LocalDateTime.now());
        return toResponseDTO(bairroRepository.save(bairro));
    }

    public BairroResponseDTO update(Integer id, BairroRequestDTO dto) {
        Optional<Bairro> optional = bairroRepository.findById(id);
        if (optional.isPresent()) {
            Bairro bairro = optional.get();
            bairro.setNome(dto.getNome());
            bairro.setCidade(dto.getCidade());
            bairro.setEstado(dto.getEstado());
            bairro.setCepInicial(dto.getCepInicial());
            bairro.setCepFinal(dto.getCepFinal());
            bairro.setUpdatedAt(LocalDateTime.now());
            return toResponseDTO(bairroRepository.save(bairro));
        }
        return null;
    }

    public boolean delete(Integer id) {
        if (bairroRepository.existsById(id)) {
            bairroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private BairroResponseDTO toResponseDTO(Bairro bairro) {
        BairroResponseDTO dto = new BairroResponseDTO();
        dto.setId(bairro.getId());
        dto.setNome(bairro.getNome());
        dto.setCidade(bairro.getCidade());
        dto.setEstado(bairro.getEstado());
        dto.setCepInicial(bairro.getCepInicial());
        dto.setCepFinal(bairro.getCepFinal());
        dto.setCreatedAt(bairro.getCreatedAt());
        dto.setUpdatedAt(bairro.getUpdatedAt());
        return dto;
    }
}
