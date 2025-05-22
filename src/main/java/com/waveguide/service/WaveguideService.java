package com.waveguide.service;

import com.waveguide.exception.ResourceNotFoundException;
import com.waveguide.exception.UnauthorizedAccessException;
import com.waveguide.model.dto.request.LayerRequest;
import com.waveguide.model.dto.request.WaveguideRequest;
import com.waveguide.model.dto.response.LayerResponse;
import com.waveguide.model.dto.response.WaveguideResponse;
import com.waveguide.model.entity.Layer;
import com.waveguide.model.entity.User;
import com.waveguide.model.entity.Waveguide;
import com.waveguide.repository.LayerRepository;
import com.waveguide.repository.WaveguideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaveguideService {
    
    private final WaveguideRepository waveguideRepository;
    private final LayerRepository layerRepository;
    private final LogService logService;

    @Transactional
    public WaveguideResponse createWaveguide(WaveguideRequest request, User currentUser) {
        // Validate request
        if (request.getNEffMax() <= request.getNEffMin()) {
            throw new IllegalArgumentException("n_eff_max must be greater than n_eff_min");
        }
        
        // Create waveguide
        Waveguide waveguide = Waveguide.builder()
                .user(currentUser)
                .nEffMin(request.getNEffMin())
                .nEffMax(request.getNEffMax())
                .build();
        
        // Add layers if provided
        if (request.getLayers() != null && !request.getLayers().isEmpty()) {
            for (LayerRequest layerRequest : request.getLayers()) {
                Layer layer = convertToLayerEntity(layerRequest);
                waveguide.addLayer(layer);
            }
        }
        
        Waveguide savedWaveguide = waveguideRepository.save(waveguide);
        
        // Log waveguide creation
        logService.logUserAction(
                currentUser, 
                "WAVEGUIDE_CREATE", 
                "Created waveguide with ID: " + savedWaveguide.getId()
        );
        
        return convertToWaveguideResponse(savedWaveguide);
    }

    @Transactional(readOnly = true)
    public Page<WaveguideResponse> getWaveguides(User currentUser, Pageable pageable) {
        Page<Waveguide> waveguidesPage = waveguideRepository.findAllByUser(currentUser, pageable);
        return waveguidesPage.map(this::convertToWaveguideResponse);
    }

    @Transactional(readOnly = true)
    public WaveguideResponse getWaveguideById(UUID waveguideId, User currentUser) {
        Waveguide waveguide = waveguideRepository.findByIdAndUser(waveguideId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Waveguide not found with id: " + waveguideId));
        
        return convertToWaveguideResponse(waveguide);
    }

    @Transactional
    public void deleteWaveguide(UUID waveguideId, User currentUser) {
        if (!waveguideRepository.existsByIdAndUser(waveguideId, currentUser)) {
            throw new ResourceNotFoundException("Waveguide not found with id: " + waveguideId);
        }
        
        waveguideRepository.deleteByIdAndUser(waveguideId, currentUser);
        
        // Log waveguide deletion
        logService.logUserAction(
                currentUser,
                "WAVEGUIDE_DELETE",
                "Deleted waveguide with ID: " + waveguideId
        );
    }

    @Transactional
    public LayerResponse addLayer(UUID waveguideId, LayerRequest request, User currentUser) {
        Waveguide waveguide = waveguideRepository.findByIdAndUser(waveguideId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Waveguide not found with id: " + waveguideId));
        
        Layer layer = convertToLayerEntity(request);
        waveguide.addLayer(layer);
        
        Waveguide savedWaveguide = waveguideRepository.save(waveguide);
        Layer savedLayer = savedWaveguide.getLayers().get(savedWaveguide.getLayers().size() - 1);
        
        // Log layer addition
        logService.logUserAction(
                currentUser,
                "LAYER_CREATE",
                "Added layer to waveguide with ID: " + waveguideId
        );
        
        return convertToLayerResponse(savedLayer);
    }

    @Transactional
    public LayerResponse updateLayer(UUID waveguideId, UUID layerId, LayerRequest request, User currentUser) {
        Waveguide waveguide = waveguideRepository.findByIdAndUser(waveguideId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Waveguide not found with id: " + waveguideId));
        
        Layer layer = layerRepository.findByIdAndWaveguideId(layerId, waveguideId)
                .orElseThrow(() -> new ResourceNotFoundException("Layer not found with id: " + layerId));
        
        // Update layer properties
        layer.setE(request.getE());
        layer.setReEps(request.getReEps());
        layer.setImEps(request.getImEps());
        layer.setD(request.getD());
        
        Layer updatedLayer = layerRepository.save(layer);
        
        // Log layer update
        logService.logUserAction(
                currentUser,
                "LAYER_UPDATE",
                "Updated layer with ID: " + layerId + " in waveguide with ID: " + waveguideId
        );
        
        return convertToLayerResponse(updatedLayer);
    }

    @Transactional
    public void deleteLayer(UUID waveguideId, UUID layerId, User currentUser) {
        Waveguide waveguide = waveguideRepository.findByIdAndUser(waveguideId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Waveguide not found with id: " + waveguideId));
        
        Layer layer = layerRepository.findByIdAndWaveguideId(layerId, waveguideId)
                .orElseThrow(() -> new ResourceNotFoundException("Layer not found with id: " + layerId));
        
        waveguide.removeLayer(layer);
        waveguideRepository.save(waveguide);
        
        // Log layer deletion
        logService.logUserAction(
                currentUser,
                "LAYER_DELETE",
                "Deleted layer with ID: " + layerId + " from waveguide with ID: " + waveguideId
        );
    }

    // Helper methods for converting between entities and DTOs
    
    private Layer convertToLayerEntity(LayerRequest request) {
        return Layer.builder()
                .E(request.getE())
                .reEps(request.getReEps())
                .imEps(request.getImEps())
                .d(request.getD())
                .build();
    }
    
    private LayerResponse convertToLayerResponse(Layer layer) {
        return LayerResponse.builder()
                .id(layer.getId())
                .layerIndex(layer.getLayerIndex())
                .E(layer.getE())
                .reEps(layer.getReEps())
                .imEps(layer.getImEps())
                .d(layer.getD())
                .build();
    }
    
    private WaveguideResponse convertToWaveguideResponse(Waveguide waveguide) {
        List<LayerResponse> layerResponses = waveguide.getLayers().stream()
                .map(this::convertToLayerResponse)
                .collect(Collectors.toList());
        
        return WaveguideResponse.builder()
                .id(waveguide.getId())
                .nEffMin(waveguide.getNEffMin())
                .nEffMax(waveguide.getNEffMax())
                .layers(layerResponses)
                .createdAt(waveguide.getCreatedAt())
                .build();
    }
}