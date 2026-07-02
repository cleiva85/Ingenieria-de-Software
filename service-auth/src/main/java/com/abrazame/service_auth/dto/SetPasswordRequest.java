package com.abrazame.service_auth.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPasswordRequest {
    @NotBlank public String token;
    @NotBlank @Size(min = 8) public String password;
}
