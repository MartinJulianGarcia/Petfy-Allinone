package Petfy.Petfy_Back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el request de aplicación de paseador
 * Mapea: walker-application.component.ts -> applicationForm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalkerApplicationRequest {
    
    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String phone;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    // Nota: El archivo de imagen se manejará por separado en el controlador
    // private MultipartFile documentImage;
}

