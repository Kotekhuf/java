package com.waveguide.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "layers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_waveguide_layer_index",
            columnNames = {"waveguide_id", "layer_index"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waveguide_id", nullable = false)
    private Waveguide waveguide;

    @Column(name = "layer_index", nullable = false)
    @NotNull(message = "Layer index is required")
    private Integer layerIndex;

    @Column(nullable = false)
    @NotNull(message = "E is required")
    private Double E;

    @Column(nullable = false)
    @NotNull(message = "reEps is required")
    private Double reEps;

    @Column(nullable = false)
    @NotNull(message = "imEps is required")
    private Double imEps;

    @Column(nullable = false)
    @NotNull(message = "d is required")
    @Positive(message = "d must be greater than 0")
    private Double d;
}