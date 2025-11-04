package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Paseador;
import Petfy.Petfy_Back.model.Paseador.EstadoAprobacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Paseador
 * Proporciona métodos CRUD y queries personalizadas
 */
@Repository
public interface PaseadorRepository extends JpaRepository<Paseador, Long> {
    
    /**
     * Busca un paseador por su usuario
     * Se usaría en: walker-application.component.ts para verificar si ya es paseador
     */
    Optional<Paseador> findByUsuarioId(Long usuarioId);

    /**
     * Busca todos los paseadores aprobados
     * Se usaría en: request.component.ts para listar paseadores disponibles
     */
    List<Paseador> findByEstadoAprobacion(EstadoAprobacion estado);

    /**
     * Busca un paseador por el nombre de usuario
     * Se usaría en: request.component.ts para asignar paseador específico
     */
    Optional<Paseador> findByUsuarioUsername(String username);

    /**
     * Busca paseadores disponibles (aprobados y no ocupados en una fecha/hora específica)
     * Se usaría en: request.component.ts para seleccionar paseador "Aleatorio"
     */
    // List<Paseador> findPaseadoresDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}


