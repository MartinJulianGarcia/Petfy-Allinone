package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Solicitud
 */
@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    
    /**
     * Busca una solicitud pendiente o aprobada por el ID del usuario (la más reciente)
     */
    Optional<Solicitud> findFirstByUsuarioIdOrderByFechaSolicitudDesc(Long usuarioId);
    
    /**
     * Busca todas las solicitudes pendientes
     */
    List<Solicitud> findByEstado(Solicitud.EstadoSolicitud estado);
    
    /**
     * Busca todas las solicitudes de un usuario específico
     */
    List<Solicitud> findAllByUsuarioId(Long usuarioId);
    
    /**
     * Busca todas las solicitudes aprobadas por un admin específico
     */
    List<Solicitud> findByAdminAprobadorId(Long adminId);
    
    /**
     * Verifica si existe una solicitud pendiente para un usuario
     */
    boolean existsByUsuarioIdAndEstado(Long usuarioId, Solicitud.EstadoSolicitud estado);
}

