package Petfy.Petfy_Back.controller;

import Petfy.Petfy_Back.dto.request.WalkerApplicationRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.PaseadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para gestión de paseadores
 * 
 * Mapea los endpoints del frontend:
 * - walker-application.component.ts -> POST /api/paseadores/solicitar -> solicitar ser paseador
 * - request.component.ts -> GET /api/paseadores/disponibles -> obtener lista de paseadores
 */
@RestController
@RequestMapping("/api/paseadores")
@CrossOrigin(origins = "http://localhost:4200")
public class PaseadorController {

    @Autowired
    private PaseadorService paseadorService;

    /**
     * Solicitud para convertirse en paseador
     * 
     * Frontend: walker-application.component.ts -> submitApplication()
     * 
     * POST /api/paseadores/solicitar
     * Header: Authorization: Basic base64(email:password)
     * Body: FormData { phone, description, documentImage }
     * 
     * Response: { success: true, message: "..." }
     */
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/solicitar")
    public ResponseEntity<ApiResponse<Void>> solicitarSerPaseador(
            @RequestPart("request") @Valid WalkerApplicationRequest request,
            @RequestPart(value = "documentImage", required = false) MultipartFile documentImage,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        ApiResponse<Void> response = paseadorService.solicitarSerPaseador(usuarioId, request, documentImage);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene lista de paseadores disponibles
     * 
     * Frontend: request.component.ts -> walkers[] (para mostrar en el dropdown)
     * 
     * GET /api/paseadores/disponibles
     * 
     * Response: List<PaseadorResponse>
     * 
     * Nota: Este endpoint podría ser público o requerir autenticación
     */
    @GetMapping("/disponibles")
    public ResponseEntity<?> obtenerPaseadoresDisponibles() {
        // TODO: Implementar obtención de paseadores aprobados
        // 1. Buscar todos los paseadores con estado APROBADO
        // 2. Retornar lista de PaseadorResponse con nombre, calificación, etc.
        
        // List<PaseadorResponse> paseadores = paseadorService.obtenerPaseadoresDisponibles();
        // return ResponseEntity.ok(paseadores);
        
        return ResponseEntity.ok("Endpoint no implementado - mostraría lista de paseadores");
    }

    // Método auxiliar
    private Long obtenerUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }
}

