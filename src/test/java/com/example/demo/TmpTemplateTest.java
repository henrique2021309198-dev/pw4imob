package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ActiveProfiles("test")
class TmpTemplateTest {

    @Autowired
    private TemplateEngine engine;

    @Test
    void renderiza() {
        Context c = new Context();
        c.setVariables(Map.of("nome", "Maria", "itens", List.of("Plano Pro", "Suporte 24h"), "link", "https://meusite.com"));
        String html = engine.process("email/template", c);
        System.out.println(html);
        assertTrue(html.contains("Maria") && html.contains("Suporte 24h") && html.contains("https://meusite.com"));
    }
}
