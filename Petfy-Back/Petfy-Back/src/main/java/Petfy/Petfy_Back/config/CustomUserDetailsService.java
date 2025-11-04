package Petfy.Petfy_Back.config;

import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar usuarios desde la base de datos
 * 
 * Se usa en Basic Auth para:
 * - Buscar usuario por email (username en Spring Security)
 * - Validar credenciales al hacer login
 * 
 * Conecta con: auth.service.ts -> login() donde se envía email:password
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por email (usado como username en Basic Auth)
     * 
     * @param email El email del usuario (viene del header Authorization: Basic)
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRol().name())
            .build();
    }
}


