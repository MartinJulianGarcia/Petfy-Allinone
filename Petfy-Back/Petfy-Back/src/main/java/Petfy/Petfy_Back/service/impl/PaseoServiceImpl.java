package Petfy.Petfy_Back.service.impl;

import Petfy.Petfy_Back.dto.request.PaseoRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.dto.response.PaseoResponse;
import Petfy.Petfy_Back.model.Paseador;
import Petfy.Petfy_Back.model.Paseo;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.PaseadorRepository;
import Petfy.Petfy_Back.repository.PaseoRepository;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.PaseoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de paseos
 * 
 * Simula la lógica según el frontend:
 * - request.component.ts -> confirmRequest() -> crear/actualizar paseo
 * - requests.component.ts -> loadRequests() -> obtener paseos del cliente
 * - walker-requests.component.ts -> loadRequests() -> obtener paseos del paseador
 * - walker-requests.component.ts -> acceptRequest() -> aceptar paseo
 * - walker-requests.component.ts -> startWalk() -> iniciar paseo
 * - walker-requests.component.ts -> finishWalk() -> finalizar paseo
 * - history.component.ts -> loadFinalizedWalks() -> obtener paseos finalizados
 */
@Service
@Transactional
public class PaseoServiceImpl implements PaseoService {

    @Autowired
    private PaseoRepository paseoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaseadorRepository paseadorRepository;

    @Override
    public ApiResponse<PaseoResponse> crearPaseo(Long clienteId, PaseoRequest request) {
        // Buscar cliente
        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Crear nuevo paseo
        Paseo paseo = new Paseo();
        paseo.setCliente(cliente);
        paseo.setFecha(request.getDate());
        paseo.setHoraInicio(request.getStartTime());
        paseo.setHoraFin(request.getEndTime());
        paseo.setDireccion(request.getAddress());
        paseo.setEstado(Paseo.EstadoPaseo.PENDIENTE);
        paseo.setIsCompleted(false);

        // Lógica para asignar paseador
        if (request.getWalker() != null && !request.getWalker().isEmpty()) {
            if ("Aleatorio".equalsIgnoreCase(request.getWalker())) {
                // Si es aleatorio, NO asignar paseador todavía
                // El paseo queda pendiente hasta que un paseador lo acepte
                paseo.setPaseador(null);
            } else {
                // Si es paseador específico, buscar por username
                Paseador paseador = paseadorRepository.findByUsuarioUsername(request.getWalker())
                    .orElse(null);
                
                if (paseador != null && paseador.getEstadoAprobacion() == Paseador.EstadoAprobacion.APROBADO) {
                    paseo.setPaseador(paseador);
                    // El paseo queda pendiente hasta que ese paseador específico lo acepte
                } else {
                    return ApiResponse.error("Paseador no encontrado o no está aprobado");
                }
            }
        }

        // Guardar paseo
        Paseo paseoGuardado = paseoRepository.save(paseo);

        return ApiResponse.success(
            "Solicitud de paseo enviada exitosamente",
            PaseoResponse.fromEntity(paseoGuardado)
        );
    }

    @Override
    public ApiResponse<PaseoResponse> actualizarPaseo(Long paseoId, PaseoRequest request) {
        // Buscar paseo
        Paseo paseo = paseoRepository.findById(paseoId)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

        // Si el paseo está confirmado, volverlo a pendiente
        if (paseo.getEstado() == Paseo.EstadoPaseo.CONFIRMADO) {
            paseo.setEstado(Paseo.EstadoPaseo.PENDIENTE);
        }

        // Actualizar datos del paseo
        paseo.setFecha(request.getDate());
        paseo.setHoraInicio(request.getStartTime());
        paseo.setHoraFin(request.getEndTime());
        paseo.setDireccion(request.getAddress());

        // Actualizar paseador si cambió
        if (request.getWalker() != null && !request.getWalker().isEmpty()) {
            if ("Aleatorio".equalsIgnoreCase(request.getWalker())) {
                paseo.setPaseador(null);
            } else {
                Paseador paseador = paseadorRepository.findByUsuarioUsername(request.getWalker())
                    .orElse(null);
                if (paseador != null) {
                    paseo.setPaseador(paseador);
                }
            }
        }

        // Guardar cambios
        Paseo paseoActualizado = paseoRepository.save(paseo);

        return ApiResponse.success(
            "Paseo modificado exitosamente. Volverá a confirmarse automáticamente.",
            PaseoResponse.fromEntity(paseoActualizado)
        );
    }

    @Override
    public List<PaseoResponse> obtenerPaseosPorCliente(Long clienteId) {
        List<Paseo> paseos = paseoRepository.findByClienteId(clienteId);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosPendientesPorCliente(Long clienteId) {
        List<Paseo> paseos = paseoRepository.findByClienteIdAndEstado(clienteId, Paseo.EstadoPaseo.PENDIENTE);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosConfirmadosPorCliente(Long clienteId) {
        List<Paseo> paseos = paseoRepository.findByClienteIdAndEstado(clienteId, Paseo.EstadoPaseo.CONFIRMADO);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosPorPaseador(Long paseadorId) {
        List<Paseo> paseos = paseoRepository.findByPaseadorUsuarioId(paseadorId);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosPendientes() {
        // Obtener todos los paseos pendientes
        // Estos son los que pueden ver los paseadores y aceptar
        List<Paseo> paseos = paseoRepository.findByEstado(Paseo.EstadoPaseo.PENDIENTE);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosConfirmadosPorPaseador(Long paseadorId) {
        List<Paseo> paseos = paseoRepository.findByPaseadorUsuarioIdAndEstado(
            paseadorId, 
            Paseo.EstadoPaseo.CONFIRMADO
        );
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<PaseoResponse> aceptarPaseo(Long paseoId, Long paseadorId) {
        // Buscar paseo
        Paseo paseo = paseoRepository.findById(paseoId)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

        // Verificar que el paseo esté pendiente
        if (paseo.getEstado() != Paseo.EstadoPaseo.PENDIENTE) {
            return ApiResponse.error("Esta solicitud ya fue aceptada o no está disponible");
        }

        // Buscar paseador
        Paseador paseador = paseadorRepository.findById(paseadorId)
            .orElseThrow(() -> new RuntimeException("Paseador no encontrado"));

        // Verificar que el paseador esté aprobado
        if (paseador.getEstadoAprobacion() != Paseador.EstadoAprobacion.APROBADO) {
            return ApiResponse.error("No tienes permiso para aceptar paseos");
        }

        // Verificar si el paseo tiene un paseador específico asignado
        if (paseo.getPaseador() != null) {
            // Si tiene paseador específico, solo ese paseador puede aceptarlo
            if (!paseo.getPaseador().getId().equals(paseadorId)) {
                return ApiResponse.error("Este paseo está asignado a otro paseador");
            }
        }
        // Si paseador es null (Aleatorio), cualquier paseador puede aceptarlo

        // Asignar el paseador al paseo
        paseo.setPaseador(paseador);
        paseo.setEstado(Paseo.EstadoPaseo.CONFIRMADO);
        paseo.setFechaConfirmacion(LocalDateTime.now());

        // Guardar cambios
        Paseo paseoConfirmado = paseoRepository.save(paseo);

        return ApiResponse.success(
            "¡Solicitud aceptada exitosamente!",
            PaseoResponse.fromEntity(paseoConfirmado)
        );
    }

    @Override
    public ApiResponse<Void> cancelarPaseo(Long paseoId) {
        // Buscar paseo
        Paseo paseo = paseoRepository.findById(paseoId)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

        // Cambiar estado a cancelado
        paseo.setEstado(Paseo.EstadoPaseo.CANCELADO);

        // Guardar cambios
        paseoRepository.save(paseo);

        return ApiResponse.success("Solicitud cancelada exitosamente");
    }

    @Override
    public ApiResponse<PaseoResponse> iniciarPaseo(Long paseoId) {
        // Buscar paseo
        Paseo paseo = paseoRepository.findById(paseoId)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

        // Verificar que el paseo esté confirmado
        if (paseo.getEstado() != Paseo.EstadoPaseo.CONFIRMADO) {
            return ApiResponse.error("El paseo debe estar confirmado para poder iniciarlo");
        }

        // Cambiar estado a en progreso
        paseo.setEstado(Paseo.EstadoPaseo.EN_PROGRESO);
        paseo.setFechaInicio(LocalDateTime.now());

        // Guardar cambios
        Paseo paseoIniciado = paseoRepository.save(paseo);

        return ApiResponse.success(
            "¡Paseo iniciado!",
            PaseoResponse.fromEntity(paseoIniciado)
        );
    }

    @Override
    public ApiResponse<PaseoResponse> finalizarPaseo(Long paseoId) {
        // Buscar paseo
        Paseo paseo = paseoRepository.findById(paseoId)
            .orElseThrow(() -> new RuntimeException("Paseo no encontrado"));

        // Verificar que el paseo esté en progreso
        if (paseo.getEstado() != Paseo.EstadoPaseo.EN_PROGRESO) {
            return ApiResponse.error("El paseo debe estar en progreso para poder finalizarlo");
        }

        // Cambiar estado a finalizado
        paseo.setEstado(Paseo.EstadoPaseo.FINALIZADO);
        paseo.setIsCompleted(true);
        paseo.setFechaFin(LocalDateTime.now());

        // Guardar cambios
        Paseo paseoFinalizado = paseoRepository.save(paseo);

        return ApiResponse.success(
            "¡Paseo finalizado!",
            PaseoResponse.fromEntity(paseoFinalizado)
        );
    }

    @Override
    public List<PaseoResponse> obtenerPaseosFinalizadosPorCliente(Long clienteId) {
        List<Paseo> paseos = paseoRepository.findPaseosFinalizadosPorCliente(clienteId);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaseoResponse> obtenerPaseosFinalizadosPorRango(Long clienteId, LocalDate startDate, LocalDate endDate) {
        List<Paseo> paseos = paseoRepository.findPaseosPorRangoFecha(clienteId, startDate, endDate);
        return paseos.stream()
            .map(PaseoResponse::fromEntity)
            .collect(Collectors.toList());
    }
}

