package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Mensaje
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    /**
     * Busca todos los mensajes de un chat específico ordenados por fecha
     */
    List<Mensaje> findByChatIdOrderByFechaEnvioAsc(Long chatId);
    
    /**
     * Busca todos los mensajes no leídos de un chat
     */
    List<Mensaje> findByChatIdAndLeidoFalse(Long chatId);
    
    /**
     * Busca todos los mensajes de un usuario específico
     */
    List<Mensaje> findByUsuarioId(Long usuarioId);
    
    /**
     * Cuenta los mensajes no leídos de un chat
     */
    long countByChatIdAndLeidoFalse(Long chatId);
}

