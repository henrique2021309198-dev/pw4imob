package com.example.demo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dtos.FotoImovelRequestDTO;
import com.example.demo.dtos.FotoImovelResponseDTO;
import com.example.demo.dtos.ImovelResponseDTO;
import com.example.demo.models.FotoImovel;
import com.example.demo.models.ImovelModel;
import com.example.demo.repositories.FotoImovelRepository;
import com.example.demo.repositories.ImovelRepository;

@Service
public class FotoImovelService {

    @Autowired
    private FotoImovelRepository fotoImovelRepository;

    @Autowired
    private ImovelRepository imovelRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public FotoImovelResponseDTO salvar(MultipartFile arquivo, Integer imovelId, Boolean capa, Integer ordem) {
        ImovelModel imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new IllegalArgumentException("Imovel nao encontrado: " + imovelId));
        String original = StringUtils.cleanPath(arquivo.getOriginalFilename() == null ? "" : arquivo.getOriginalFilename());
        String nome = Paths.get(original).getFileName() == null ? "" : Paths.get(original).getFileName().toString();
        String contentType = arquivo.getContentType();
        if (nome.isBlank() || contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Arquivo deve ser uma imagem");
        }
        try {
            Path pasta = Paths.get(uploadDir, String.valueOf(imovelId)).toAbsolutePath().normalize();
            Files.createDirectories(pasta);
            String nomeFinal = UUID.randomUUID() + "_" + nome;
            Path destino = pasta.resolve(nomeFinal);
            arquivo.transferTo(destino);

            FotoImovel foto = new FotoImovel();
            foto.setImovel(imovel);
            foto.setNomeArquivo(nomeFinal);
            foto.setCaminho(destino.toString());
            foto.setOrdem(ordem);
            foto.setCapa(false);
            foto.setCreatedAt(LocalDateTime.now());
            foto.setUpdatedAt(LocalDateTime.now());
            FotoImovel salva = fotoImovelRepository.save(foto);
            if (Boolean.TRUE.equals(capa) || fotoImovelRepository.findByImovelId(imovelId).size() == 1) {
                return definirCapa(salva.getId());
            }
            return toResponseDTO(salva);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    @Transactional
    public FotoImovelResponseDTO definirCapa(Integer id) {
        FotoImovel foto = fotoImovelRepository.findById(id).orElse(null);
        if (foto == null) {
            return null;
        }
        if (foto.getImovel() != null) {
            for (FotoImovel outra : fotoImovelRepository.findByImovelId(foto.getImovel().getId())) {
                if (!outra.getId().equals(id) && Boolean.TRUE.equals(outra.getCapa())) {
                    outra.setCapa(false);
                    fotoImovelRepository.save(outra);
                }
            }
        }
        foto.setCapa(true);
        foto.setUpdatedAt(LocalDateTime.now());
        return toResponseDTO(fotoImovelRepository.save(foto));
    }

    public List<FotoImovelResponseDTO> findAll() {
        return fotoImovelRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FotoImovelResponseDTO> findByImovelId(Integer imovelId) {
        return fotoImovelRepository.findByImovelId(imovelId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public FotoImovelResponseDTO findById(Integer id) {
        Optional<FotoImovel> foto = fotoImovelRepository.findById(id);
        return foto.map(this::toResponseDTO).orElse(null);
    }

    public FotoImovelResponseDTO create(FotoImovelRequestDTO dto) {
        FotoImovel foto = new FotoImovel();
        updateEntity(foto, dto);
        foto.setCreatedAt(LocalDateTime.now());
        foto.setUpdatedAt(LocalDateTime.now());
        return toResponseDTO(fotoImovelRepository.save(foto));
    }

    public FotoImovelResponseDTO update(Integer id, FotoImovelRequestDTO dto) {
        Optional<FotoImovel> optional = fotoImovelRepository.findById(id);
        if (optional.isPresent()) {
            FotoImovel foto = optional.get();
            if (dto.getOrdem() != null) {
                foto.setOrdem(dto.getOrdem());
            }
            if (Boolean.FALSE.equals(dto.getCapa())) {
                foto.setCapa(false);
            }
            foto.setUpdatedAt(LocalDateTime.now());
            FotoImovel salva = fotoImovelRepository.save(foto);
            if (Boolean.TRUE.equals(dto.getCapa())) {
                return definirCapa(id);
            }
            return toResponseDTO(salva);
        }
        return null;
    }

    public boolean delete(Integer id) {
        FotoImovel foto = fotoImovelRepository.findById(id).orElse(null);
        if (foto == null) {
            return false;
        }
        fotoImovelRepository.delete(foto);
        try {
            if (foto.getCaminho() != null) {
                Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
                Path arquivo = Paths.get(foto.getCaminho()).toAbsolutePath().normalize();
                if (arquivo.startsWith(base)) {
                    Files.deleteIfExists(arquivo);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover arquivo", e);
        }
        return true;
    }

    private void updateEntity(FotoImovel foto, FotoImovelRequestDTO dto) {
        foto.setNomeArquivo(dto.getNomeArquivo());
        foto.setCaminho(dto.getCaminho());
        foto.setCapa(dto.getCapa());
        foto.setOrdem(dto.getOrdem());

        if (dto.getImovelId() != null) {
            Optional<ImovelModel> imovel = imovelRepository.findById(dto.getImovelId());
            imovel.ifPresent(foto::setImovel);
        }
    }

    private FotoImovelResponseDTO toResponseDTO(FotoImovel foto) {
        FotoImovelResponseDTO dto = new FotoImovelResponseDTO();
        dto.setId(foto.getId());
        dto.setNomeArquivo(foto.getNomeArquivo());
        if (foto.getImovel() != null && foto.getNomeArquivo() != null) {
            dto.setUrl("/uploads/" + foto.getImovel().getId() + "/"
                    + UriUtils.encodePathSegment(foto.getNomeArquivo(), StandardCharsets.UTF_8));
        }
        dto.setCapa(foto.getCapa());
        dto.setOrdem(foto.getOrdem());
        dto.setCreatedAt(foto.getCreatedAt());
        dto.setUpdatedAt(foto.getUpdatedAt());

        if (foto.getImovel() != null) {
            ImovelResponseDTO imovelDto = new ImovelResponseDTO();
            imovelDto.setId(foto.getImovel().getId());
            imovelDto.setTitulo(foto.getImovel().getTitulo());
            dto.setImovel(imovelDto);
        }

        return dto;
    }
}
