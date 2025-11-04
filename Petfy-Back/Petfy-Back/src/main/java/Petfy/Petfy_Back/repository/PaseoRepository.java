package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Paseo;
import Petfy.Petfy_Back.model.Paseo.EstadoPaseo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Paseo
 * Proporciona métodos CRUD y queries personalizadas
 */
@Repository
public interface PaseoRepository extends JpaRepository<Paseo, Long> {
    
    /**
     * Busca todos los paseos de un cliente
     * Se usaría en: requests.component.ts para mostrar paseos del cliente
     */
    List<Paseo> findByClienteId(Long clienteId);

    /**
     * Busca todos los paseos de un paseador
     * Se usaría en: walker-requests.component.ts para mostrar paseos del paseador
     */
    List<Paseo> findByPaseadorUsuarioId(Long paseadorId);

    /**
     * Busca paseos por estado
     * Se usaría en: requests.component.ts para filtrar por "pending" o "confirmed"
     */
    List<Paseo> findByEstado(EstadoPaseo estado);

    /**
     * Busca paseos pendientes de un cliente
     * Se usaría en: requests.component.ts -> pendingRequests
     */
    List<Paseo> findByClienteIdAndEstado(Long clienteId, EstadoPaseo estado);

    /**
     * Busca paseos confirmados de un paseador
     * Se usaría en: walker-requests.component.ts -> confirmedRequests
     */
    List<Paseo> findByPaseadorUsuarioIdAndEstado(Long paseadorId, EstadoPaseo estado);

    /**
     * Busca paseos finalizados de un cliente
     * Se usaría en: history.component.ts -> finalizedWalks
     */
    @Query("SELECT p FROM Paseo p WHERE p.cliente.id = :clienteId AND p.isCompleted = true")
    List<Paseo> findPaseosFinalizadosPorCliente(@Param("clienteId") Long clienteId);

    /**
     * Busca paseos finalizados de un paseador
     * Se usaría en: history.component.ts para paseadores
     */
    @Query("SELECT p FROM Paseo p WHERE p.paseador.usuario.id = :paseadorId AND p.isCompleted = true")
    List<Paseo> findPaseosFinalizadosPorPaseador(@Param("paseadorId") Long paseadorId);

    /**
     * Busca paseos por rango de fechas
     * Se usaría en: history.component.ts -> filterWalks() para filtrar por fecha
     */
    @Query("SELECT p FROM Paseo p WHERE p.cliente.id = :clienteId AND p.fecha BETWEEN :startDate AND :endDate AND p.isCompleted = true")
    List<Paseo> findPaseosPorRangoFecha(@Param("clienteId") Long clienteId, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
}


