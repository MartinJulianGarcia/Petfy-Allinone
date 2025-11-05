package Petfy.Petfy_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Solicitud
 * Representa una solicitud pendiente de un usuario para convertirse en paseador
 * Solo puede ser aprobada por un usuario con rol ADMIN
 */
@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que solicita ser paseador

    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "La descripción es requerida")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "ruta_documento")
    private String rutaDocumento; // Ruta donde se almacena la imagen del documento

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_aprobador_id")
    private Usuario adminAprobador; // Usuario con rol ADMIN que aprobó la solicitud (null si está pendiente)

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "comentario_admin", columnDefinition = "TEXT")
    private String comentarioAdmin; // Comentario opcional del admin al aprobar/rechazar

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
    }

    public enum EstadoSolicitud {
        PENDIENTE,      // Pendiente de revisión por un admin
        APROBADA,       // Aprobada por un admin
        RECHAZADA       // Rechazada por un admin
    }
}

