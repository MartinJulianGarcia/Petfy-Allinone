package Petfy.Petfy_Back.service.impl;

import Petfy.Petfy_Back.dto.request.LoginRequest;
import Petfy.Petfy_Back.dto.request.RegisterRequest;
import Petfy.Petfy_Back.dto.request.UpdateUsuarioRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.UsuarioResponse;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de autenticación
 * 
 * Simula la lógica de registro y login según el frontend:
 * - register.component.ts -> register()
 * - login.component.ts -> login()
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<UsuarioResponse> register(RegisterRequest request) {
        // Validar que las contraseñas coincidan
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.error("Las contraseñas no coinciden");
        }

        // Verificar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Ya existe un usuario con este email");
        }

        // Verificar que el username no exista
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("Ya existe un usuario con este nombre de usuario");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        usuario.setRol(Usuario.RolUsuario.CUSTOMER); // Por defecto todos son clientes

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Retornar respuesta exitosa
        return ApiResponse.success(
            "Usuario registrado exitosamente",
            UsuarioResponse.fromEntity(usuarioGuardado)
        );
    }

    @Override
    public ApiResponse<UsuarioResponse> login(LoginRequest request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElse(null);

        if (usuario == null) {
            return ApiResponse.error("Email o contraseña incorrectos");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ApiResponse.error("Email o contraseña incorrectos");
        }

        // Retornar respuesta exitosa con el usuario
        return ApiResponse.success(
            "Inicio de sesión exitoso",
            UsuarioResponse.fromEntity(usuario)
        );
    }

    @Override
    public UsuarioResponse getCurrentUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return UsuarioResponse.fromEntity(usuario);
    }

    @Override
    public void logout() {
        // En Basic Auth stateless, el logout se maneja principalmente en el frontend
        // Eliminando las credenciales del localStorage
        // Este método está aquí por si se implementa JWT en el futuro
    }

    @Override
    public ApiResponse<UsuarioResponse> updateUsuario(String email, UpdateUsuarioRequest request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el nuevo username no exista (si es diferente al actual)
        if (!usuario.getUsername().equals(request.getUsername())) {
            if (usuarioRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.error("Ya existe un usuario con este nombre de usuario");
            }
        }

        // Actualizar solo el nombre de usuario
        usuario.setUsername(request.getUsername());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return ApiResponse.success(
            "Nombre de usuario actualizado exitosamente",
            UsuarioResponse.fromEntity(usuarioActualizado)
        );
    }
}


