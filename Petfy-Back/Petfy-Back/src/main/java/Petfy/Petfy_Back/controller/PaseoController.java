package Petfy.Petfy_Back.controller;

import Petfy.Petfy_Back.dto.request.PaseoRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.PaseoResponse;
import Petfy.Petfy_Back.model.Paseo;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.model.Paseador;
import Petfy.Petfy_Back.repository.PaseadorRepository;
import Petfy.Petfy_Back.repository.PaseoRepository;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.PaseoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para gestión de paseos
 * 
 * Mapea los endpoints del frontend:
 * - request.component.ts -> POST /api/paseos -> crear paseo
 * - request.component.ts -> PUT /api/paseos/{id} -> actualizar paseo
 * - requests.component.ts -> GET /api/paseos/cliente -> obtener paseos del cliente
 * - walker-requests.component.ts -> GET /api/paseos/paseador -> obtener paseos del paseador
 * - history.component.ts -> GET /api/paseos/finalizados -> obtener paseos finalizados
 */
@RestController
@RequestMapping("/api/paseos")
@CrossOrigin(origins = "http://localhost:4200")
public class PaseoController {

    @Autowired
    private PaseoService paseoService;

    /**
     * Crea una nueva solicitud de paseo
     * 
     * Frontend: request.component.ts -> confirmRequest()
     * 
     * POST /api/paseos
     * Header: Authorization: Basic base64(email:password)
     * Body: { date, startTime, endTime, address, walker }
     * 
     * Response: { success: true, message: "...", data: PaseoResponse }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaseoResponse>> crearPaseo(
            @Valid @RequestBody PaseoRequest request,
            Authentication authentication) {
        Long clienteId = obtenerUsuarioId(authentication);
        ApiResponse<PaseoResponse> response = paseoService.crearPaseo(clienteId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza un paseo existente
     * 
     * Frontend: request.component.ts -> confirmRequest() cuando isEditing = true
     * 
     * PUT /api/paseos/{id}
     * Header: Authorization: Basic base64(email:password)
     * Body: { date, startTime, endTime, address, walker }
     * 
     * Response: { success: true, message: "...", data: PaseoResponse }
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaseoResponse>> actualizarPaseo(
            @PathVariable Long id,
            @Valid @RequestBody PaseoRequest request,
            Authentication authentication) {
        // Verificar que el paseo pertenezca al cliente autenticado
        Long clienteId = obtenerUsuarioId(authentication);
        Paseo paseo = paseoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));
        
        if (!paseo.getCliente().getId().equals(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("No tienes permiso para modificar este paseo"));
        }
        
        ApiResponse<PaseoResponse> response = paseoService.actualizarPaseo(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los paseos del cliente autenticado
     * 
     * Frontend: requests.component.ts -> loadRequests()
     * 
     * GET /api/paseos/cliente
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/cliente")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosPorCliente(Authentication authentication) {
        // TODO: Implementar obtención de paseos
        Long clienteId = obtenerUsuarioId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosPorCliente(clienteId);
        return ResponseEntity.ok(paseos);
    }

    /**
     * Obtiene paseos pendientes del cliente
     * 
     * Frontend: requests.component.ts -> pendingRequests
     * 
     * GET /api/paseos/cliente/pendientes
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/cliente/pendientes")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosPendientes(Authentication authentication) {
        // TODO: Implementar obtención de paseos pendientes
        Long clienteId = obtenerUsuarioId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosPendientesPorCliente(clienteId);
        return ResponseEntity.ok(paseos);
    }

    /**
     * Obtiene paseos confirmados del cliente
     * 
     * Frontend: requests.component.ts -> confirmedRequests
     * 
     * GET /api/paseos/cliente/confirmados
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/cliente/confirmados")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosConfirmados(Authentication authentication) {
        // TODO: Implementar obtención de paseos confirmados
        Long clienteId = obtenerUsuarioId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosConfirmadosPorCliente(clienteId);
        return ResponseEntity.ok(paseos);
    }

    /**
     * Obtiene todos los paseos pendientes (para paseadores)
     * 
     * Frontend: walker-requests.component.ts -> pendingRequests
     * 
     * GET /api/paseos/pendientes
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosPendientes() {
        // TODO: Implementar obtención de todos los paseos pendientes
        // Solo paseadores pueden ver estos
        List<PaseoResponse> paseos = paseoService.obtenerPaseosPendientes();
        return ResponseEntity.ok(paseos);
    }

    /**
     * Obtiene paseos confirmados del paseador autenticado
     * 
     * Frontend: walker-requests.component.ts -> confirmedRequests
     * 
     * GET /api/paseos/paseador/confirmados
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/paseador/confirmados")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosConfirmadosPorPaseador(Authentication authentication) {
        // TODO: Implementar obtención de paseos confirmados del paseador
        Long paseadorId = obtenerPaseadorId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosConfirmadosPorPaseador(paseadorId);
        return ResponseEntity.ok(paseos);
    }

    /**
     * Acepta un paseo pendiente
     * 
     * Frontend: walker-requests.component.ts -> acceptRequest()
     * 
     * POST /api/paseos/{id}/aceptar
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: { success: true, message: "...", data: PaseoResponse }
     */
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<ApiResponse<PaseoResponse>> aceptarPaseo(
            @PathVariable Long id,
            Authentication authentication) {
        // TODO: Implementar lógica de aceptación
        // 1. Verificar que el usuario autenticado sea paseador
        // 2. Verificar que el paseo esté pendiente
        // 3. Asignar el paseador al paseo
        // 4. Cambiar estado a CONFIRMADO
        // 5. Retornar PaseoResponse
        
        Long paseadorId = obtenerPaseadorId(authentication);
        ApiResponse<PaseoResponse> response = paseoService.aceptarPaseo(id, paseadorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela un paseo
     * 
     * Frontend: requests.component.ts -> cancelRequest()
     * 
     * DELETE /api/paseos/{id}
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: { success: true, message: "..." }
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelarPaseo(
            @PathVariable Long id,
            Authentication authentication) {
        // TODO: Implementar lógica de cancelación
        // 1. Verificar que el paseo pertenezca al cliente autenticado
        // 2. Cambiar estado a CANCELADO o eliminar
        // 3. Retornar confirmación
        
        ApiResponse<Void> response = paseoService.cancelarPaseo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia un paseo
     * 
     * Frontend: walker-requests.component.ts -> startWalk()
     * 
     * POST /api/paseos/{id}/iniciar
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: { success: true, message: "...", data: PaseoResponse }
     */
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<PaseoResponse>> iniciarPaseo(
            @PathVariable Long id,
            Authentication authentication) {
        // TODO: Implementar lógica de inicio
        // 1. Verificar que el paseo pertenezca al paseador autenticado
        // 2. Verificar que el paseo esté confirmado
        // 3. Cambiar estado a EN_PROGRESO
        // 4. Guardar fecha de inicio
        // 5. Retornar PaseoResponse
        
        ApiResponse<PaseoResponse> response = paseoService.iniciarPaseo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Finaliza un paseo
     * 
     * Frontend: walker-requests.component.ts -> finishWalk()
     * 
     * POST /api/paseos/{id}/finalizar
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: { success: true, message: "...", data: PaseoResponse }
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<PaseoResponse>> finalizarPaseo(
            @PathVariable Long id,
            Authentication authentication) {
        // TODO: Implementar lógica de finalización
        // 1. Verificar que el paseo pertenezca al paseador autenticado
        // 2. Verificar que el paseo esté en progreso
        // 3. Cambiar estado a FINALIZADO
        // 4. Marcar isCompleted = true
        // 5. Guardar fecha de fin
        // 6. Retornar PaseoResponse
        
        ApiResponse<PaseoResponse> response = paseoService.finalizarPaseo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene paseos finalizados del cliente
     * 
     * Frontend: history.component.ts -> loadFinalizedWalks()
     * 
     * GET /api/paseos/cliente/finalizados
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/cliente/finalizados")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosFinalizados(Authentication authentication) {
        // TODO: Implementar obtención de paseos finalizados
        Long clienteId = obtenerUsuarioId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosFinalizadosPorCliente(clienteId);
        return ResponseEntity.ok(paseos);
    }

    /**
     * Obtiene paseos finalizados filtrados por rango de fechas
     * 
     * Frontend: history.component.ts -> filterWalks()
     * 
     * GET /api/paseos/cliente/finalizados?startDate=2025-01-01&endDate=2025-12-31
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: List<PaseoResponse>
     */
    @GetMapping("/cliente/finalizados/filtrados")
    public ResponseEntity<List<PaseoResponse>> obtenerPaseosFinalizadosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        // TODO: Implementar filtrado por rango de fechas
        Long clienteId = obtenerUsuarioId(authentication);
        List<PaseoResponse> paseos = paseoService.obtenerPaseosFinalizadosPorRango(clienteId, startDate, endDate);
        return ResponseEntity.ok(paseos);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaseadorRepository paseadorRepository;

    @Autowired
    private PaseoRepository paseoRepository;

    // Métodos auxiliares
    private Long obtenerUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }

    private Long obtenerPaseadorId(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Paseador paseador = paseadorRepository.findByUsuarioId(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Usuario no es paseador"));
        return paseador.getId();
    }
}

