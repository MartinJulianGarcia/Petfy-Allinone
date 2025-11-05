package Petfy.Petfy_Back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el request de actualización de usuario
 * Solo permite actualizar el nombre de usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequest {
    
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    private String username;
}

