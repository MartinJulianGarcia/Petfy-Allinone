package Petfy.Petfy_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Paseador
 * Representa la información adicional de los usuarios que son paseadores
 */
@Entity
@Table(name = "paseadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paseador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "La descripción es requerida")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ruta_documento")
    private String rutaDocumento; // Ruta donde se almacena la imagen del documento

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAprobacion estadoAprobacion = EstadoAprobacion.PENDIENTE;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @Column(name = "total_calificaciones")
    private Integer totalCalificaciones = 0;

    @OneToMany(mappedBy = "paseador", cascade = CascadeType.ALL)
    private List<Paseo> paseos;

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
    }

    public enum EstadoAprobacion {
        PENDIENTE,      // Pendiente de aprobación
        APROBADO,       // Aprobado como paseador
        RECHAZADO       // Rechazado
    }
}

