// This is the controller for managing Bairro entities
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

import com.example.demo.dtos.BairroRequestDTO;
import com.example.demo.dtos.BairroResponseDTO;
import com.example.demo.services.BairroService;

@RestController
@RequestMapping(value = "/bairros")
public class BairroController {

    @Autowired
    private BairroService bairroService;

    @GetMapping
    public ResponseEntity<List<BairroResponseDTO>> findAll() {
        return ResponseEntity.ok(bairroService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<BairroResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(bairroService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BairroResponseDTO> findById(@PathVariable Integer id) {
        BairroResponseDTO dto = bairroService.findById(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<BairroResponseDTO> create(@RequestBody BairroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bairroService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BairroResponseDTO> update(@PathVariable Integer id, @RequestBody BairroRequestDTO dto) {
        BairroResponseDTO updated = bairroService.update(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (bairroService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
