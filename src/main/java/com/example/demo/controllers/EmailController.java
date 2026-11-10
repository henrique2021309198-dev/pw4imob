package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.mail.MailException;
import org.springframework.web.client.RestClientException;

import com.example.demo.services.EmailService;

@RestController
@RequestMapping("/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Endpoint apenas para teste: envia sempre para mailgun.test-to
    @PostMapping("/teste")
    public ResponseEntity<String> teste() {
        try {
            return ResponseEntity.ok(emailService.enviarTeste());
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @PostMapping("/teste-smtp")
    public ResponseEntity<String> testeSmtp() {
        try {
            emailService.enviarTesteSmtp();
            return ResponseEntity.ok("enviado");
        } catch (MailException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }
}
