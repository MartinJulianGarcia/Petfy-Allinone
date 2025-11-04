package Petfy.Petfy_Back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para Basic Authentication
 * 
 * Se conectaría con el frontend enviando:
 * - Authorization: Basic base64(email:password) en el header
 * 
 * El frontend (auth.service.ts) debería:
 * 1. Al hacer login, recibir las credenciales del backend
 * 2. Guardar email:password en base64 en localStorage o sessionStorage
 * 3. Enviar este header en cada petición HTTP al backend
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configuración del SecurityFilterChain
     * Define qué endpoints son públicos y cuáles requieren autenticación
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configurar CORS
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless para APIs REST
            .userDetailsService(userDetailsService) // Usar nuestro servicio personalizado
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (no requieren autenticación)
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Endpoints protegidos (requieren autenticación)
                .requestMatchers("/api/auth/**").authenticated()
                .requestMatchers("/api/paseos/**").authenticated()
                .requestMatchers("/api/paseadores/**").authenticated()
                .requestMatchers("/api/calificaciones/**").authenticated()
                .requestMatchers("/api/usuarios/**").authenticated()
                
                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {}); // Habilitar Basic Auth

        return http.build();
    }

    /**
     * Configuración de CORS para permitir peticiones desde el frontend Angular
     * 
     * Se conectaría con el frontend permitiendo:
     * - Origin: http://localhost:4200 (Angular dev server)
     * - Métodos: GET, POST, PUT, DELETE, PATCH
     * - Headers: Authorization, Content-Type
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Frontend Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Encoder de contraseñas usando BCrypt
     * 
     * Se usaría para:
     * - Encriptar contraseñas al registrar usuarios (register)
     * - Comparar contraseñas al hacer login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
