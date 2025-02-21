package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordDto {

    @NotBlank
    @Size(min = 6, message = "Password should have at least 6 characters")
    @JsonProperty("newPassword")
    private String newPassword;

    @NotBlank
    @JsonProperty("matchPassword")
    private String matchPassword;

    @NotBlank
    @JsonProperty("token")
    private String token;

    public PasswordDto() {
    }

    public PasswordDto(String validToken, String newPassword, String matchPassword) {
        this.token = validToken;
        this.newPassword = newPassword;
        this.matchPassword = matchPassword;
    }

    // Getters and setters
    public String getNewPassword() {
        return newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}