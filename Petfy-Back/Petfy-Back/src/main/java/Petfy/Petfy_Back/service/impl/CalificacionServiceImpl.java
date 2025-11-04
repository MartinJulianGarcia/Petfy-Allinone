package Petfy.Petfy_Back.service.impl;

import Petfy.Petfy_Back.dto.request.CalificacionRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.model.Calificacion;
import Petfy.Petfy_Back.model.Paseador;
import Petfy.Petfy_Back.model.Paseo;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.CalificacionRepository;
import Petfy.Petfy_Back.repository.PaseadorRepository;
import Petfy.Petfy_Back.repository.PaseoRepository;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de calificaciones
 * 
 * Simula la lógica según el frontend:
 * - history.component.ts -> submitRating() -> calificar paseo o app
 */
@Service
@Transactional
public class CalificacionServiceImpl implements CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaseoRepository paseoRepository;

    @Autowired
    private PaseadorRepository paseadorRepository;

    @Override
    public ApiResponse<Void> crearCalificacion(Long usuarioId, CalificacionRequest request) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Determinar el tipo de calificación
        Calificacion.TipoCalificacion tipo;
        if ("app".equalsIgnoreCase(request.getTipo())) {
            tipo = Calificacion.TipoCalificacion.APP;
        } else if ("walk".equalsIgnoreCase(request.getTipo()) || request.getPaseoId() != null) {
            tipo = Calificacion.TipoCalificacion.PASEO;
        } else {
            return ApiResponse.error("Tipo de calificación no válido");
        }

        // Si es calificación de paseo, verificar que el paseo exista y esté finalizado
        Paseo paseo = null;
        if (tipo == Calificacion.TipoCalificacion.PASEO) {
            if (request.getPaseoId() == null) {
                return ApiResponse.error("El ID del paseo es requerido para calificar un paseo");
            }

            paseo = paseoRepository.findById(request.getPaseoId())
                .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

            // Verificar que el paseo pertenezca al usuario (cliente)
            if (!paseo.getCliente().getId().equals(usuarioId)) {
                return ApiResponse.error("Solo puedes calificar tus propios paseos");
            }

            // Verificar que el paseo esté finalizado
            if (!paseo.getIsCompleted() || paseo.getEstado() != Paseo.EstadoPaseo.FINALIZADO) {
                return ApiResponse.error("Solo puedes calificar paseos finalizados");
            }

            // Verificar que no haya calificado ya este paseo
            if (calificacionRepository.findByPaseoId(paseo.getId()).isPresent()) {
                return ApiResponse.error("Ya has calificado este paseo");
            }
        }

        // Crear calificación
        Calificacion calificacion = new Calificacion();
        calificacion.setUsuario(usuario);
        calificacion.setPaseo(paseo);
        calificacion.setCalificacion(request.getCalificacion());
        calificacion.setTipo(tipo);
        calificacion.setComentario(request.getComentario());

        // Guardar calificación
        calificacionRepository.save(calificacion);

        // Si es calificación de paseo, actualizar el promedio del paseador
        if (tipo == Calificacion.TipoCalificacion.PASEO && paseo.getPaseador() != null) {
            actualizarCalificacionPromedioPaseador(paseo.getPaseador().getId());
        }

        String mensaje = tipo == Calificacion.TipoCalificacion.APP
            ? String.format("¡Gracias por calificar nuestra app con %d estrella%s!", 
                request.getCalificacion(), request.getCalificacion() > 1 ? "s" : "")
            : String.format("¡Gracias por calificar el paseo con %d estrella%s!", 
                request.getCalificacion(), request.getCalificacion() > 1 ? "s" : "");

        return ApiResponse.success(mensaje);
    }

    /**
     * Actualiza la calificación promedio de un paseador
     */
    private void actualizarCalificacionPromedioPaseador(Long paseadorId) {
        // Calcular promedio de calificaciones
        Double promedio = calificacionRepository.calcularPromedioCalificacionesPaseador(paseadorId);
        
        if (promedio != null) {
            // Obtener el paseador y actualizar su calificación promedio
            Paseador paseador = paseadorRepository.findById(paseadorId)
                .orElse(null);
            
            if (paseador != null) {
                // Contar total de calificaciones
                long totalCalificaciones = calificacionRepository.findCalificacionesPorPaseador(paseadorId).size();
                
                paseador.setCalificacionPromedio(promedio);
                paseador.setTotalCalificaciones((int) totalCalificaciones);
                paseadorRepository.save(paseador);
            }
        }
    }
}

