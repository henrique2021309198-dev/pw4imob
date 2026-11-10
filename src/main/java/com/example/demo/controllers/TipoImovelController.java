package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.TipoImovelRequestDTO;
import com.example.demo.dtos.TipoImovelResponseDTO;
import com.example.demo.services.TipoImovelService;

@RestController
@RequestMapping(value = "/tipos-imoveis")
public class TipoImovelController {

    @Autowired
    private TipoImovelService tipoImovelService;

    @GetMapping
    public ResponseEntity<List<TipoImovelResponseDTO>> findAll() {
        return ResponseEntity.ok(tipoImovelService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TipoImovelResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(tipoImovelService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoImovelResponseDTO> findById(@PathVariable Integer id) {
        TipoImovelResponseDTO dto = tipoImovelService.findById(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<TipoImovelResponseDTO> create(@RequestBody TipoImovelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoImovelService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoImovelResponseDTO> update(@PathVariable Integer id, @RequestBody TipoImovelRequestDTO dto) {
        TipoImovelResponseDTO updated = tipoImovelService.update(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (tipoImovelService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
