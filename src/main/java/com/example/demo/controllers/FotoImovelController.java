package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.FotoImovelRequestDTO;
import com.example.demo.dtos.FotoImovelResponseDTO;
import com.example.demo.services.FotoImovelService;

@RestController
@RequestMapping(value = "/fotos-imoveis")
public class FotoImovelController {

    @Autowired
    private FotoImovelService fotoImovelService;

    @GetMapping
    public ResponseEntity<List<FotoImovelResponseDTO>> findAll() {
        return ResponseEntity.ok(fotoImovelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> findById(@PathVariable Integer id) {
        FotoImovelResponseDTO dto = fotoImovelService.findById(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/imovel/{imovelId}")
    public ResponseEntity<List<FotoImovelResponseDTO>> findByImovelId(@PathVariable Integer imovelId) {
        return ResponseEntity.ok(fotoImovelService.findByImovelId(imovelId));
    }

    @PostMapping
    public ResponseEntity<FotoImovelResponseDTO> create(@RequestBody FotoImovelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fotoImovelService.create(dto));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoImovelResponseDTO> upload(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("imovelId") Integer imovelId,
            @RequestParam(value = "capa", required = false) Boolean capa,
            @RequestParam(value = "ordem", required = false) Integer ordem) {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(fotoImovelService.salvar(arquivo, imovelId, capa, ordem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/capa")
    public ResponseEntity<FotoImovelResponseDTO> definirCapa(@PathVariable Integer id) {
        FotoImovelResponseDTO dto = fotoImovelService.definirCapa(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> update(@PathVariable Integer id, @RequestBody FotoImovelRequestDTO dto) {
        FotoImovelResponseDTO updated = fotoImovelService.update(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (fotoImovelService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
