package Petfy.Petfy_Back.dto.response;

import Petfy.Petfy_Back.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la response de Usuario
 * Mapea: Usuario -> Frontend (auth.service.ts -> User)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String username;
    private String email;
    private String role; // "customer" o "walker"
    private LocalDateTime fechaRegistro;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getUsername(),
            usuario.getEmail(),
            usuario.getRol().name().toLowerCase(),
            usuario.getFechaRegistro()
        );
    }
}


