package com.waveguide.service;

import com.waveguide.exception.ResourceNotFoundException;
import com.waveguide.model.dto.request.LayerRequest;
import com.waveguide.model.dto.request.WaveguideRequest;
import com.waveguide.model.dto.response.WaveguideResponse;
import com.waveguide.model.entity.Layer;
import com.waveguide.model.entity.User;
import com.waveguide.model.entity.Waveguide;
import com.waveguide.repository.LayerRepository;
import com.waveguide.repository.WaveguideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WaveguideServiceTest {

    @Mock
    private WaveguideRepository waveguideRepository;
    
    @Mock
    private LayerRepository layerRepository;
    
    @Mock
    private LogService logService;
    
    @InjectMocks
    private WaveguideService waveguideService;
    
    private User testUser;
    private Waveguide testWaveguide;
    private WaveguideRequest validRequest;
    private Layer testLayer;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .createdAt(LocalDateTime.now())
                .build();
        
        testLayer = Layer.builder()
                .id(UUID.randomUUID())
                .E(1.0)
                .reEps(2.0)
                .imEps(0.1)
                .d(5.0)
                .layerIndex(0)
                .build();
        
        testWaveguide = Waveguide.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .nEffMin(3.39)
                .nEffMax(3.9)
                .createdAt(LocalDateTime.now())
                .layers(new ArrayList<>())
                .build();
        testLayer.setWaveguide(testWaveguide);
        testWaveguide.getLayers().add(testLayer);
        
        LayerRequest layerRequest = LayerRequest.builder()
                .E(1.0)
                .reEps(2.0)
                .imEps(0.1)
                .d(5.0)
                .build();
        
        validRequest = WaveguideRequest.builder()
                .nEffMin(3.39)
                .nEffMax(3.9)
                .layers(List.of(layerRequest))
                .build();
    }
    
    @Test
    void createWaveguide_WithValidRequest_ShouldReturnWaveguideResponse() {
        // Arrange
        when(waveguideRepository.save(any(Waveguide.class))).thenReturn(testWaveguide);
        
        // Act
        WaveguideResponse response = waveguideService.createWaveguide(validRequest, testUser);
        
        // Assert
        assertNotNull(response);
        assertEquals(testWaveguide.getId(), response.getId());
        assertEquals(testWaveguide.getNEffMin(), response.getNEffMin());
        assertEquals(testWaveguide.getNEffMax(), response.getNEffMax());
        assertEquals(1, response.getLayers().size());
        
        verify(waveguideRepository).save(any(Waveguide.class));
        verify(logService).logUserAction(eq(testUser), eq("WAVEGUIDE_CREATE"), anyString());
    }
    
    @Test
    void createWaveguide_WithInvalidEffectiveIndices_ShouldThrowException() {
        // Arrange
        WaveguideRequest invalidRequest = WaveguideRequest.builder()
                .nEffMin(3.9)
                .nEffMax(3.39)  // Max less than min
                .layers(validRequest.getLayers())
                .build();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            waveguideService.createWaveguide(invalidRequest, testUser);
        });
        
        verify(waveguideRepository, never()).save(any(Waveguide.class));
    }
    
    @Test
    void getWaveguideById_WithExistingId_ShouldReturnWaveguideResponse() {
        // Arrange
        when(waveguideRepository.findByIdAndUser(testWaveguide.getId(), testUser))
                .thenReturn(Optional.of(testWaveguide));
        
        // Act
        WaveguideResponse response = waveguideService.getWaveguideById(testWaveguide.getId(), testUser);
        
        // Assert
        assertNotNull(response);
        assertEquals(testWaveguide.getId(), response.getId());
        assertEquals(testWaveguide.getNEffMin(), response.getNEffMin());
        assertEquals(testWaveguide.getNEffMax(), response.getNEffMax());
        assertEquals(1, response.getLayers().size());
        
        verify(waveguideRepository).findByIdAndUser(testWaveguide.getId(), testUser);
    }
    
    @Test
    void getWaveguideById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(waveguideRepository.findByIdAndUser(nonExistingId, testUser))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            waveguideService.getWaveguideById(nonExistingId, testUser);
        });
        
        verify(waveguideRepository).findByIdAndUser(nonExistingId, testUser);
    }
    
    @Test
    void getWaveguides_ShouldReturnPageOfWaveguideResponses() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Waveguide> waveguidePage = new PageImpl<>(List.of(testWaveguide));
        
        when(waveguideRepository.findAllByUser(testUser, pageable)).thenReturn(waveguidePage);
        
        // Act
        Page<WaveguideResponse> responsePage = waveguideService.getWaveguides(testUser, pageable);
        
        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(testWaveguide.getId(), responsePage.getContent().get(0).getId());
        
        verify(waveguideRepository).findAllByUser(testUser, pageable);
    }
    
    @Test
    void deleteWaveguide_WithExistingId_ShouldDeleteWaveguide() {
        // Arrange
        when(waveguideRepository.existsByIdAndUser(testWaveguide.getId(), testUser)).thenReturn(true);
        
        // Act
        waveguideService.deleteWaveguide(testWaveguide.getId(), testUser);
        
        // Assert
        verify(waveguideRepository).existsByIdAndUser(testWaveguide.getId(), testUser);
        verify(waveguideRepository).deleteByIdAndUser(testWaveguide.getId(), testUser);
        verify(logService).logUserAction(eq(testUser), eq("WAVEGUIDE_DELETE"), anyString());
    }
    
    @Test
    void deleteWaveguide_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(waveguideRepository.existsByIdAndUser(nonExistingId, testUser)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            waveguideService.deleteWaveguide(nonExistingId, testUser);
        });
        
        verify(waveguideRepository).existsByIdAndUser(nonExistingId, testUser);
        verify(waveguideRepository, never()).deleteByIdAndUser(any(), any());
    }
}