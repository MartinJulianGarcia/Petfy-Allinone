package Petfy.Petfy_Back.controller;

import Petfy.Petfy_Back.dto.request.CalificacionRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.CalificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestión de calificaciones
 * 
 * Mapea los endpoints del frontend:
 * - history.component.ts -> POST /api/calificaciones -> calificar paseo o app
 */
@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    /**
     * Crea una nueva calificación
     * 
     * Frontend: history.component.ts -> submitRating()
     * 
     * POST /api/calificaciones
     * Header: Authorization: Basic base64(email:password)
     * Body: { calificacion, tipo, paseoId, comentario }
     * 
     * Response: { success: true, message: "..." }
     */
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crearCalificacion(
            @Valid @RequestBody CalificacionRequest request,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        ApiResponse<Void> response = calificacionService.crearCalificacion(usuarioId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Método auxiliar
    private Long obtenerUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }
}

