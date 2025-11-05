package Petfy.Petfy_Back.service.impl;

import Petfy.Petfy_Back.dto.request.WalkerApplicationRequest;
import Petfy.Petfy_Back.dto.response.ApiResponse;
import Petfy.Petfy_Back.model.Paseador;
import Petfy.Petfy_Back.model.Usuario;
import Petfy.Petfy_Back.repository.PaseadorRepository;
import Petfy.Petfy_Back.repository.UsuarioRepository;
import Petfy.Petfy_Back.service.PaseadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementación del servicio de paseadores
 * 
 * Simula la lógica según el frontend:
 * - walker-application.component.ts -> submitApplication()
 */
@Service
@Transactional
public class PaseadorServiceImpl implements PaseadorService {

    @Autowired
    private PaseadorRepository paseadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public ApiResponse<Void> solicitarSerPaseador(Long usuarioId, WalkerApplicationRequest request, MultipartFile documentImage) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el usuario no sea ya paseador
        if (paseadorRepository.findByUsuarioId(usuarioId).isPresent()) {
            return ApiResponse.error("Ya eres un paseador registrado");
        }

        // Guardar imagen del documento
        String rutaDocumento = null;
        if (documentImage != null && !documentImage.isEmpty()) {
            try {
                rutaDocumento = guardarArchivo(documentImage);
            } catch (IOException e) {
                return ApiResponse.error("Error al guardar el documento: " + e.getMessage());
            }
        }

        // Crear registro de Paseador
        Paseador paseador = new Paseador();
        paseador.setUsuario(usuario);
        paseador.setTelefono(request.getPhone());
        paseador.setDescripcion(request.getDescription());
        paseador.setRutaDocumento(rutaDocumento);
        
        // Validar código de aprobación
        // Si el código es "cascuino", aprobar automáticamente, sino dejar en PENDIENTE
        String validationCode = request.getValidationCode();
        
        // Validar que el código sea exactamente "cascuino" (case-insensitive)
        boolean codigoValido = validationCode != null 
            && !validationCode.trim().isEmpty() 
            && validationCode.trim().equalsIgnoreCase("cascuino");
        
        if (codigoValido) {
            paseador.setEstadoAprobacion(Paseador.EstadoAprobacion.APROBADO);
        } else {
            // Si el código es incorrecto o no existe, dejar en PENDIENTE
            paseador.setEstadoAprobacion(Paseador.EstadoAprobacion.PENDIENTE);
        }

        // Guardar paseador
        paseadorRepository.save(paseador);

        // Actualizar rol del usuario a WALKER solo si fue aprobado
        if (paseador.getEstadoAprobacion() == Paseador.EstadoAprobacion.APROBADO) {
            usuario.setRol(Usuario.RolUsuario.WALKER);
            // Sincronizar la relación bidireccional
            usuario.setPaseador(paseador);
            // Guardar y forzar persistencia inmediata
            Usuario usuarioActualizado = usuarioRepository.save(usuario);
            usuarioRepository.flush(); // Forzar flush para asegurar persistencia inmediata
            
            return ApiResponse.success("¡Solicitud aprobada! Ya eres un paseador. Ahora puedes ver las solicitudes de los clientes.");
        } else {
            // Si está pendiente, solo guardar el paseador sin cambiar el rol
            // Sincronizar la relación bidireccional
            usuario.setPaseador(paseador);
            usuarioRepository.save(usuario);
            usuarioRepository.flush();
            
            return ApiResponse.success("Solicitud enviada. Tu solicitud quedó en revisión y será evaluada por nuestro equipo.");
        }
    }

    /**
     * Guarda un archivo en el sistema de archivos
     */
    private String guardarArchivo(MultipartFile file) throws IOException {
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
}


