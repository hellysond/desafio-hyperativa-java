package br.com.hellyson.controller;

import br.com.hellyson.model.LoginRequestDTO;
import br.com.hellyson.service.KeycloakAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthProxyController {

    private final KeycloakAuthService keycloakAuthService;

    public AuthProxyController(KeycloakAuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Map<String, Object> token = keycloakAuthService.getToken(request);
        return ResponseEntity.ok(token);
    }
}