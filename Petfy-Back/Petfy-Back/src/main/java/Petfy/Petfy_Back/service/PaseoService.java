package Petfy.Petfy_Back.service;

import Petfy.Petfy_Back.dto.request.PaseoRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.PaseoResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de paseos
 * 
 * Mapea los métodos del frontend:
 * - request.component.ts -> confirmRequest() -> crear paseo
 * - requests.component.ts -> loadRequests() -> obtener paseos del cliente
 * - walker-requests.component.ts -> loadRequests() -> obtener paseos del paseador
 * - history.component.ts -> loadFinalizedWalks() -> obtener paseos finalizados
 */
public interface PaseoService {
    
    /**
     * Crea una nueva solicitud de paseo
     * 
     * Frontend: request.component.ts -> confirmRequest()
     * 
     * @param clienteId ID del cliente que solicita el paseo
     * @param request Datos del paseo (fecha, hora, dirección, paseador)
     * @return ApiResponse con el paseo creado
     */
    ApiResponse<PaseoResponse> crearPaseo(Long clienteId, PaseoRequest request);

    /**
     * Actualiza un paseo existente
     * 
     * Frontend: request.component.ts -> confirmRequest() cuando isEditing = true
     * 
     * @param paseoId ID del paseo a actualizar
     * @param request Nuevos datos del paseo
     * @return ApiResponse con el paseo actualizado
     */
    ApiResponse<PaseoResponse> actualizarPaseo(Long paseoId, PaseoRequest request);

    /**
     * Obtiene todos los paseos de un cliente
     * 
     * Frontend: requests.component.ts -> loadRequests()
     * 
     * @param clienteId ID del cliente
     * @return Lista de paseos del cliente
     */
    List<PaseoResponse> obtenerPaseosPorCliente(Long clienteId);

    /**
     * Obtiene todos los paseos pendientes de un cliente
     * 
     * Frontend: requests.component.ts -> pendingRequests
     * 
     * @param clienteId ID del cliente
     * @return Lista de paseos pendientes
     */
    List<PaseoResponse> obtenerPaseosPendientesPorCliente(Long clienteId);

    /**
     * Obtiene todos los paseos confirmados de un cliente
     * 
     * Frontend: requests.component.ts -> confirmedRequests
     * 
     * @param clienteId ID del cliente
     * @return Lista de paseos confirmados
     */
    List<PaseoResponse> obtenerPaseosConfirmadosPorCliente(Long clienteId);

    /**
     * Obtiene todos los paseos de un paseador
     * 
     * Frontend: walker-requests.component.ts -> loadRequests()
     * 
     * @param paseadorId ID del paseador
     * @return Lista de paseos del paseador
     */
    List<PaseoResponse> obtenerPaseosPorPaseador(Long paseadorId);

    /**
     * Obtiene todos los paseos pendientes disponibles para paseadores
     * 
     * Frontend: walker-requests.component.ts -> pendingRequests
     * 
     * @return Lista de paseos pendientes
     */
    List<PaseoResponse> obtenerPaseosPendientes();

    /**
     * Obtiene todos los paseos confirmados de un paseador
     * 
     * Frontend: walker-requests.component.ts -> confirmedRequests
     * 
     * @param paseadorId ID del paseador
     * @return Lista de paseos confirmados
     */
    List<PaseoResponse> obtenerPaseosConfirmadosPorPaseador(Long paseadorId);

    /**
     * Acepta un paseo pendiente (paseador acepta la solicitud)
     * 
     * Frontend: walker-requests.component.ts -> acceptRequest()
     * 
     * @param paseoId ID del paseo a aceptar
     * @param paseadorId ID del paseador que acepta
     * @return ApiResponse con el paseo confirmado
     */
    ApiResponse<PaseoResponse> aceptarPaseo(Long paseoId, Long paseadorId);

    /**
     * Cancela un paseo
     * 
     * Frontend: requests.component.ts -> cancelRequest()
     * 
     * @param paseoId ID del paseo a cancelar
     * @return ApiResponse confirmando la cancelación
     */
    ApiResponse<Void> cancelarPaseo(Long paseoId);

    /**
     * Inicia un paseo (marca como en progreso)
     * 
     * Frontend: walker-requests.component.ts -> startWalk()
     * 
     * @param paseoId ID del paseo a iniciar
     * @return ApiResponse con el paseo actualizado
     */
    ApiResponse<PaseoResponse> iniciarPaseo(Long paseoId);

    /**
     * Finaliza un paseo (marca como finalizado)
     * 
     * Frontend: walker-requests.component.ts -> finishWalk()
     * 
     * @param paseoId ID del paseo a finalizar
     * @return ApiResponse con el paseo finalizado
     */
    ApiResponse<PaseoResponse> finalizarPaseo(Long paseoId);

    /**
     * Obtiene paseos finalizados de un cliente
     * 
     * Frontend: history.component.ts -> loadFinalizedWalks()
     * 
     * @param clienteId ID del cliente
     * @return Lista de paseos finalizados
     */
    List<PaseoResponse> obtenerPaseosFinalizadosPorCliente(Long clienteId);

    /**
     * Obtiene paseos finalizados filtrados por rango de fechas
     * 
     * Frontend: history.component.ts -> filterWalks()
     * 
     * @param clienteId ID del cliente
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de paseos finalizados en el rango
     */
    List<PaseoResponse> obtenerPaseosFinalizadosPorRango(Long clienteId, LocalDate startDate, LocalDate endDate);
}


