package Petfy.Petfy_Back.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el request de calificación
 * Mapea: history.component.ts -> rating
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionRequest {
    
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    private String tipo; // "app" o "walk"

    private Long paseoId; // Solo si es calificación de paseo

    private String comentario;
}


