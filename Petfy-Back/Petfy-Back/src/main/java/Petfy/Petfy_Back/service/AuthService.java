package Petfy.Petfy_Back.service;

import Petfy.Petfy_Back.dto.request.LoginRequest;
import Petfy.Petfy_Back.dto.request.RegisterRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.UsuarioResponse;

/**
 * Interfaz del servicio de autenticación
 * 
 * Mapea los métodos del frontend:
 * - auth.service.ts -> login() -> login(credentials)
 * - auth.service.ts -> register() -> register(userData)
 * - auth.service.ts -> logout() -> logout()
 * - auth.service.ts -> getCurrentUser() -> getCurrentUser()
 */
public interface AuthService {
    
    /**
     * Registra un nuevo usuario
     * 
     * Frontend: register.component.ts -> authService.register()
     * 
     * @param request Datos del usuario a registrar
     * @return ApiResponse con el usuario creado o mensaje de error
     */
    ApiResponse<UsuarioResponse> register(RegisterRequest request);

    /**
     * Autentica un usuario existente
     * 
     * Frontend: login.component.ts -> authService.login()
     * 
     * @param request Credenciales de login (email y password)
     * @return ApiResponse con el usuario autenticado o mensaje de error
     */
    ApiResponse<UsuarioResponse> login(LoginRequest request);

    /**
     * Obtiene el usuario actual autenticado
     * 
     * Frontend: auth.service.ts -> getCurrentUser()
     * 
     * @param email Email del usuario autenticado
     * @return UsuarioResponse con los datos del usuario
     */
    UsuarioResponse getCurrentUser(String email);

    /**
     * Cierra sesión del usuario
     * 
     * Frontend: auth.service.ts -> logout()
     * 
     * Nota: En Basic Auth stateless, el logout se maneja en el frontend
     * eliminando las credenciales almacenadas
     */
    void logout();
}


