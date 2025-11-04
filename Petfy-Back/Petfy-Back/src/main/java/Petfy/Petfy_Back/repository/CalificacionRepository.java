package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Calificacion;
import Petfy.Petfy_Back.model.Calificacion.TipoCalificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Calificacion
 * Proporciona métodos CRUD y queries personalizadas
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    
    /**
     * Busca calificaciones de un paseo específico
     * Se usaría en: history.component.ts para mostrar calificación de un paseo
     */
    Optional<Calificacion> findByPaseoId(Long paseoId);

    /**
     * Busca todas las calificaciones de un usuario
     * Se usaría en: profile.component.ts para mostrar historial de calificaciones
     */
    List<Calificacion> findByUsuarioId(Long usuarioId);

    /**
     * Busca calificaciones por tipo
     * Se usaría en: history.component.ts para diferenciar calificaciones de app vs paseos
     */
    List<Calificacion> findByTipo(TipoCalificacion tipo);

    /**
     * Calcula el promedio de calificaciones de un paseador
     * Se usaría en: Paseador.calificacionPromedio para actualizar estadísticas
     */
    @Query("SELECT AVG(c.calificacion) FROM Calificacion c WHERE c.paseo.paseador.id = :paseadorId AND c.tipo = 'PASEO'")
    Double calcularPromedioCalificacionesPaseador(@Param("paseadorId") Long paseadorId);

    /**
     * Busca calificaciones de paseos de un paseador
     * Se usaría en: walker-requests.component.ts para mostrar calificaciones recibidas
     */
    @Query("SELECT c FROM Calificacion c WHERE c.paseo.paseador.id = :paseadorId AND c.tipo = 'PASEO'")
    List<Calificacion> findCalificacionesPorPaseador(@Param("paseadorId") Long paseadorId);
}


