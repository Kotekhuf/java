package com.waveguide.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "waveguides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waveguide {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @NotNull(message = "nEffMin is required")
    private Double nEffMin;

    @Column(nullable = false)
    @NotNull(message = "nEffMax is required")
    private Double nEffMax;

    @OneToMany(mappedBy = "waveguide", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Layer> layers = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
        layer.setWaveguide(this);
        // Set the layer index to the current size of the layers list
        layer.setLayerIndex(layers.size() - 1);
    }

    public void removeLayer(Layer layer) {
        layers.remove(layer);
        layer.setWaveguide(null);
        // Reindex the remaining layers
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).setLayerIndex(i);
        }
    }
}