package Petfy.Petfy_Back.service;

import Petfy.Petfy_Back.dto.request.CalificacionRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;

/**
 * Interfaz del servicio de calificaciones
 * 
 * Mapea los métodos del frontend:
 * - history.component.ts -> submitRating() -> calificar paseo o app
 */
public interface CalificacionService {
    
    /**
     * Crea una nueva calificación
     * 
     * Frontend: history.component.ts -> submitRating()
     * 
     * @param usuarioId ID del usuario que califica
     * @param request Datos de la calificación (puntaje, tipo, comentario)
     * @return ApiResponse confirmando la calificación
     */
    ApiResponse<Void> crearCalificacion(Long usuarioId, CalificacionRequest request);

    /**
     * Obtiene la calificación promedio de un paseador
     * 
     * Frontend: walker-requests.component.ts (para mostrar calificación del paseador)
     * 
     * @param paseadorId ID del paseador
     * @return Calificación promedio
     */
    // Double obtenerCalificacionPromedioPaseador(Long paseadorId);
}


