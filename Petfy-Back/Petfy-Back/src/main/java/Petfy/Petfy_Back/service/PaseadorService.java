package Petfy.Petfy_Back.service;

import Petfy.Petfy_Back.dto.request.WalkerApplicationRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz del servicio de paseadores
 * 
 * Mapea los métodos del frontend:
 * - walker-application.component.ts -> submitApplication()
 * - request.component.ts -> walkers[] (lista de paseadores disponibles)
 */
public interface PaseadorService {
    
    /**
     * Procesa la solicitud de un usuario para convertirse en paseador
     * 
     * Frontend: walker-application.component.ts -> submitApplication()
     * 
     * @param usuarioId ID del usuario que solicita ser paseador
     * @param request Datos de la solicitud (teléfono, descripción)
     * @param documentImage Imagen del documento de identidad
     * @return ApiResponse confirmando la solicitud
     */
    ApiResponse<Void> solicitarSerPaseador(Long usuarioId, WalkerApplicationRequest request, MultipartFile documentImage);

    /**
     * Obtiene todos los paseadores aprobados disponibles
     * 
     * Frontend: request.component.ts -> walkers[] (para mostrar lista de paseadores)
     * 
     * @return Lista de paseadores aprobados
     */
    // List<PaseadorResponse> obtenerPaseadoresDisponibles();

    /**
     * Obtiene un paseador aleatorio disponible para una fecha/hora específica
     * 
     * Frontend: request.component.ts -> confirmRequest() cuando walker === "Aleatorio"
     * 
     * @param fecha Fecha del paseo
     * @param horaInicio Hora de inicio
     * @param horaFin Hora de fin
     * @return PaseadorResponse del paseador asignado
     */
    // PaseadorResponse obtenerPaseadorAleatorio(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}


