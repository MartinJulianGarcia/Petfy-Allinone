package Petfy.Petfy_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Calificacion
 * Representa las calificaciones que los usuarios dan a los paseos o a la aplicación
 */
@Entity
@Table(name = "calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "paseo_id")
    private Paseo paseo; // Null si es calificación de la app

    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCalificacion tipo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion;

    @PrePersist
    protected void onCreate() {
        fechaCalificacion = LocalDateTime.now();
    }

    public enum TipoCalificacion {
        PASEO,      // Calificación de un paseo específico
        APP         // Calificación general de la aplicación
    }
}


