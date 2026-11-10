package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String domain;
    private final String testTo;
    private final String from;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${spring.mail.username}") String from,
            @Value("${mailgun.base-url}") String baseUrl,
            @Value("${mailgun.domain}") String domain,
            @Value("${mailgun.api-key:}") String apiKey,
            @Value("${mailgun.test-to}") String testTo) {
        this.domain = domain;
        this.testTo = testTo;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("api", apiKey))
                .build();
    }

    public String enviar(String para, String assunto, String texto) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("from", "Imobiliaria <postmaster@" + domain + ">");
        form.add("to", para);
        form.add("subject", assunto);
        form.add("text", texto);

        return restClient.post()
                .uri("/v3/{domain}/messages", domain)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(String.class);
    }

    public void enviarEmailTexto(String para, String assunto, String corpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(corpo);
        mailSender.send(message);
    }

    public void enviarEmailComTemplate(String para, String assunto, String template,
            java.util.Map<String, Object> variaveis) throws MessagingException {
        Context context = new Context();
        context.setVariables(variaveis);
        String html = templateEngine.process(template, context);

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(html, true);
        mailSender.send(mime);
    }

    public void enviarTesteSmtp() {
        enviarEmailTexto(testTo, "Teste de e-mail (SMTP)", "E-mail de teste enviado via SMTP.");
    }

    public String enviarTeste() {
        return enviar(testTo, "Teste de e-mail", "E-mail de teste enviado pela API do Mailgun.");
    }
}
