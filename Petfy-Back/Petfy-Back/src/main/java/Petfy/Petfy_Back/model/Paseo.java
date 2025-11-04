package Petfy.Petfy_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad Paseo
 * Representa una solicitud/confirmación de paseo de mascota
 */
@Entity
@Table(name = "paseos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paseo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "paseador_id")
    private Paseador paseador;

    @NotNull(message = "La fecha es requerida")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es requerida")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es requerida")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotBlank(message = "La dirección es requerida")
    @Column(nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPaseo estado = EstadoPaseo.PENDIENTE;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoPaseo {
        PENDIENTE,      // Pendiente de confirmación
        CONFIRMADO,     // Confirmado por el paseador
        EN_PROGRESO,    // Paseo en progreso
        FINALIZADO,     // Paseo finalizado
        CANCELADO       // Paseo cancelado
    }
}


