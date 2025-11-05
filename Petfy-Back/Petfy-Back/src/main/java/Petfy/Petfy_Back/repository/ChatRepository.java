package Petfy.Petfy_Back.repository;

import Petfy.Petfy_Back.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Chat
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    /**
     * Busca un chat entre dos usuarios específicos
     */
    Optional<Chat> findByUsuario1IdAndUsuario2Id(Long usuario1Id, Long usuario2Id);
    
    /**
     * Busca todos los chats de un usuario (ya sea como usuario1 o usuario2)
     */
    List<Chat> findByUsuario1IdOrUsuario2Id(Long usuario1Id, Long usuario2Id);
    
    /**
     * Busca todos los chats de un usuario específico
     */
    List<Chat> findByUsuario1Id(Long usuarioId);
    
    /**
     * Busca todos los chats donde el usuario es usuario2
     */
    List<Chat> findByUsuario2Id(Long usuarioId);
}

