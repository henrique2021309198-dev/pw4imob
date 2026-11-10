package com.example.demo.controllers;

import java.net.URI;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dtos.SaberMaisRequestDTO;
import com.example.demo.models.ImovelModel;
import com.example.demo.services.EmailService;
import com.example.demo.services.ImovelService;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    private ImovelService service;

    @Autowired
    private EmailService emailService;

    @PostMapping(value = "/sabermais/{id}")
    public ResponseEntity<String> saberMais(@PathVariable Integer id, @RequestBody SaberMaisRequestDTO dto) {
        ImovelModel imovel = service.find(id);
        if (imovel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email do cliente é obrigatório");
        }
        Map<String, Object> vars = new HashMap<>();
        vars.put("nome", dto.getNome());
        vars.put("telefone", dto.getTelefone());
        vars.put("mensagem", dto.getMensagem());
        vars.put("titulo", imovel.getTitulo());
        vars.put("descricao", imovel.getDescricao());
        vars.put("finalidade", imovel.getFinalidade());
        vars.put("precoVenda", imovel.getPrecoVenda());
        vars.put("precoAluguel", imovel.getPrecoAluguel());
        vars.put("dormitorios", imovel.getDormitorios());
        vars.put("banheiros", imovel.getBanheiros());
        vars.put("garagem", imovel.getGaragem());
        vars.put("areaTotal", imovel.getAreaTotal());
        vars.put("endereco", imovel.getEndereco());
        vars.put("tipo", imovel.getTipoImovel() != null ? imovel.getTipoImovel().getNome() : null);
        vars.put("bairro", imovel.getBairro() != null ? imovel.getBairro().getNome() : null);
        try {
            emailService.enviarEmailComTemplate(dto.getEmail(), "Informações sobre o imóvel: " + imovel.getTitulo(),
                    "email/sabermais", vars);
            return ResponseEntity.ok("Email enviado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<ImovelModel>> getAll() {
        List<ImovelModel> list = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/finalidades")
    public ResponseEntity<List<String>> getFinalidades() {
        return ResponseEntity.ok(service.getFinalidades());
    }

    @GetMapping("/pagina")
    public ResponseEntity<?> search(
            @RequestParam(required = false) Integer tipoImovelId,
            @RequestParam(required = false) Integer bairroId,
            @RequestParam(required = false) String finalidade,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "recentes") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        if (page < 0 || size < 1 || size > 50) {
            return ResponseEntity.badRequest().body(Map.of("message", "Página deve ser >= 0 e tamanho entre 1 e 50."));
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ordenação deve ser 'asc' ou 'desc'."));
        }

        String sortProperty = switch (sort) {
            case "recentes" -> "id";
            case "precoVenda" -> "precoVenda";
            case "precoAluguel" -> "precoAluguel";
            case "titulo" -> "titulo";
            default -> null;
        };
        if (sortProperty == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Ordenação deve ser 'recentes', 'precoVenda', 'precoAluguel' ou 'titulo'."));
        }

        Page<ImovelModel> result = service.search(
                tipoImovelId,
                bairroId,
                finalidade,
                precoMin,
                precoMax,
                PageRequest.of(page, size, Sort.by(sortDirection, sortProperty)));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ImovelModel> find(@PathVariable Integer id) {
        ImovelModel model = service.find(id);
        if (model != null) {
            return ResponseEntity.status(HttpStatus.OK).body(model);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody ImovelModel model) {
        model = service.insert(model);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(model.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@RequestBody ImovelModel model, @PathVariable Integer id) {
        try {
            model.setId(id);
            model = service.update(model);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
