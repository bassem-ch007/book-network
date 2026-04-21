package com.bassem.bsn.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "Firstname is mandatory")
    @NotBlank
    private String firstName;
    @NotEmpty(message = "Lastname is mandatory")
    @NotBlank
    private String lastName;
    @NotEmpty(message = "Email is mandatory")
    @NotBlank
    @Email(message ="Email is not formatted")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotBlank
    @Size(min = 8 ,message = "At least 8 characters long")
    private String password;
}
