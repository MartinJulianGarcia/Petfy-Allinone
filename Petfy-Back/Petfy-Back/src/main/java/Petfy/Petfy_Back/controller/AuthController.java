package Petfy.Petfy_Back.controller;

import Petfy.Petfy_Back.dto.request.LoginRequest;
import Petfy.Petfy_Back.dto.request.RegisterRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.UsuarioResponse;
import Petfy.Petfy_Back.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación
 * 
 * Mapea los endpoints del frontend:
 * - login.component.ts -> POST /api/auth/login
 * - register.component.ts -> POST /api/auth/register
 * - auth.service.ts -> GET /api/auth/current-user
 * - auth.service.ts -> POST /api/auth/logout
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Registro de nuevo usuario
     * 
     * Frontend: register.component.ts -> onSubmit() -> authService.register()
     * 
     * POST /api/auth/register
     * Body: { username, email, password, confirmPassword }
     * 
     * Response: { success: true/false, message: "...", data: UsuarioResponse }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest request) {
        ApiResponse<UsuarioResponse> response = authService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Login de usuario
     * 
     * Frontend: login.component.ts -> onSubmit() -> authService.login()
     * 
     * POST /api/auth/login
     * Body: { email, password }
     * 
     * Response: { success: true/false, message: "...", data: UsuarioResponse }
     * 
     * Nota: En Basic Auth, las credenciales también se envían en el header Authorization
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UsuarioResponse>> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<UsuarioResponse> response = authService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Obtiene el usuario actual autenticado
     * 
     * Frontend: auth.service.ts -> getCurrentUser()
     * 
     * GET /api/auth/current-user
     * Header: Authorization: Basic base64(email:password)
     * 
     * Response: UsuarioResponse
     */
    @GetMapping("/current-user")
    public ResponseEntity<UsuarioResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // Email del usuario autenticado
        UsuarioResponse usuario = authService.getCurrentUser(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Logout de usuario
     * 
     * Frontend: auth.service.ts -> logout()
     * 
     * POST /api/auth/logout
     * 
     * Nota: En Basic Auth stateless, el logout se maneja en el frontend
     * eliminando las credenciales del localStorage
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // TODO: En Basic Auth stateless, el logout es principalmente del lado del cliente
        // Se puede usar para invalidar tokens si se implementa JWT en el futuro
        
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente"));
    }
}

