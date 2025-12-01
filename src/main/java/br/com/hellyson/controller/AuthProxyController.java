package br.com.hellyson.controller;

import br.com.hellyson.model.LoginRequestDTO;
import br.com.hellyson.service.KeycloakAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "API for user authentication and token retrieval")
public class AuthProxyController {

    private final KeycloakAuthService keycloakAuthService;

    public AuthProxyController(KeycloakAuthService keycloakAuthService) {
        this.keycloakAuthService = keycloakAuthService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user using Keycloak and returns an access token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{ \"access_token\": \"eyJhbGciOi...\", \"refresh_token\": \"...\", \"expires_in\": 300 }"))),
                    @ApiResponse(responseCode = "400", description = "Invalid login request or missing fields"),
                    @ApiResponse(responseCode = "401", description = "Authentication failed")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "User login credentials", required = true)
            @RequestBody LoginRequestDTO request) {

        Map<String, Object> token = keycloakAuthService.getToken(request);
        return ResponseEntity.ok(token);
    }
}
