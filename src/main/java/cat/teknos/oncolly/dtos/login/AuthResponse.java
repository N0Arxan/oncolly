package cat.teknos.oncolly.dtos.login;

public record AuthResponse(
        String token,
        String role,
        String userId
) {}
