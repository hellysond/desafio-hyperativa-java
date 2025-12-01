package br.com.hellyson.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Schema(description = "Username of the user", example = "user", defaultValue = "user")
    private String username;
    @Schema(description = "Password of the user", example = "password", defaultValue = "password")
    private String password;

}
