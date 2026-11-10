package com.example.demo;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.models.ImovelModel;
import com.example.demo.repositories.ImovelRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityRulesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImovelRepository imovelRepository;

    @Test
    void allowsAnonymousPropertyListing() throws Exception {
        mockMvc.perform(get("/imoveis"))
                .andExpect(status().isOk());
    }

    @Test
    void allowsAnonymousPaginatedPropertySearch() throws Exception {
        mockMvc.perform(get("/imoveis/pagina")
                        .param("page", "0")
                        .param("size", "12")
                        .param("sort", "recentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void rejectsInvalidPropertyPageSize() throws Exception {
        mockMvc.perform(get("/imoveis/pagina").param("size", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void filtersPropertiesByPurposeAndPriceAndListsAvailablePurposes() throws Exception {
        ImovelModel imovel = new ImovelModel();
        imovel.setTitulo("Casa de teste");
        imovel.setFinalidade("Residencial");
        imovel.setPrecoVenda(new BigDecimal("250000.00"));
        imovelRepository.save(imovel);

        try {
            mockMvc.perform(get("/imoveis/pagina")
                            .param("finalidade", "residencial")
                            .param("precoMin", "200000")
                            .param("precoMax", "300000")
                            .param("sort", "precoVenda")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].titulo", is("Casa de teste")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
            mockMvc.perform(get("/imoveis/finalidades"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", contains("Residencial")));
        } finally {
            imovelRepository.delete(imovel);
        }
    }

    @Test
    void allowsAnonymousPropertyFilterOptions() throws Exception {
        mockMvc.perform(get("/bairros"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tipos-imoveis"))
                .andExpect(status().isOk());
    }

    @Test
    void allowsAnonymousPropertyPhotoLookup() throws Exception {
        mockMvc.perform(get("/fotos-imoveis/imovel/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void allowsAnonymousPropertyContactRequest() throws Exception {
        mockMvc.perform(post("/imoveis/sabermais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Teste",
                                  "email": "teste@example.com",
                                  "telefone": "999999999",
                                  "mensagem": "Tenho interesse"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deniesAnonymousPropertyCreation() throws Exception {
        mockMvc.perform(post("/imoveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deniesAnonymousUserListing() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicRegistrationCannotAssignAdminRole() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Teste",
                                  "email": "teste@example.com",
                                  "senha": "senha-segura",
                                  "tipo": "ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", is("USER")));
    }

    @Test
    void allowsAngularDevelopmentOrigin() throws Exception {
        mockMvc.perform(options("/imoveis")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));
    }

    @Test
    void allowsAngularLoopbackAddress() throws Exception {
        mockMvc.perform(options("/imoveis")
                        .header(HttpHeaders.ORIGIN, "http://127.0.0.1:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:4200"));
    }
}
