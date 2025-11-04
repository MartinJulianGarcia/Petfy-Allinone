package Petfy.Petfy_Back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para el request de creación/modificación de paseo
 * Mapea: request.component.ts -> RequestData
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaseoRequest {
    
    @NotNull(message = "La fecha es requerida")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es requerida")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es requerida")
    private LocalTime endTime;

    @NotBlank(message = "La dirección es requerida")
    private String address;

    private String walker; // Puede ser "Aleatorio" o el nombre de un paseador específico
}


