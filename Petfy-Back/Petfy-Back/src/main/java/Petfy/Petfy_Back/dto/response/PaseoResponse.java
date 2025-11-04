package Petfy.Petfy_Back.dto.response;

import Petfy.Petfy_Back.model.Paseo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para la response de Paseo
 * Mapea: Paseo -> Frontend (request.component.ts -> RequestData)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaseoResponse {
    
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    private String walker; // Nombre del paseador
    private String cliente; // Nombre del cliente
    private String status; // "pending", "confirmed", "in_progress", "finished", "cancelled"
    private LocalDateTime fechaCreacion;
    private Boolean isCompleted;

    public static PaseoResponse fromEntity(Paseo paseo) {
        return new PaseoResponse(
            paseo.getId(),
            paseo.getFecha(),
            paseo.getHoraInicio(),
            paseo.getHoraFin(),
            paseo.getDireccion(),
            paseo.getPaseador() != null ? paseo.getPaseador().getUsuario().getUsername() : null,
            paseo.getCliente() != null ? paseo.getCliente().getUsername() : null,
            paseo.getEstado().name().toLowerCase(),
            paseo.getFechaCreacion(),
            paseo.getIsCompleted()
        );
    }
}


