package com.waveguide.repository;

import com.waveguide.model.entity.Layer;
import com.waveguide.model.entity.Waveguide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LayerRepository extends JpaRepository<Layer, UUID> {
    
    List<Layer> findAllByWaveguideOrderByLayerIndexAsc(Waveguide waveguide);
    
    Optional<Layer> findByIdAndWaveguideId(UUID id, UUID waveguideId);
    
    boolean existsByIdAndWaveguideId(UUID id, UUID waveguideId);
    
    void deleteByIdAndWaveguideId(UUID id, UUID waveguideId);
    
    int countByWaveguide(Waveguide waveguide);
    
    Optional<Layer> findByWaveguideIdAndLayerIndex(UUID waveguideId, Integer layerIndex);
}