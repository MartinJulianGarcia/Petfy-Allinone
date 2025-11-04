package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona métodos CRUD y queries personalizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por email
     * Se usaría en: auth.service.ts -> login() para buscar usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por username
     * Se usaría en: register.component.ts para verificar si el username ya existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado
     * Se usaría en: register.component.ts para validar email único
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el username dado
     * Se usaría en: register.component.ts para validar username único
     */
    boolean existsByUsername(String username);
}


