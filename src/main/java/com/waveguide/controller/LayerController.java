package com.waveguide.controller;

import com.waveguide.model.dto.request.LayerRequest;
import com.waveguide.model.dto.response.LayerResponse;
import com.waveguide.model.entity.User;
import com.waveguide.security.CurrentUser;
import com.waveguide.service.WaveguideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/waveguides/{waveguideId}/layers")
@RequiredArgsConstructor
@Tag(name = "Layers", description = "Operations for managing waveguide layers")
public class LayerController {

    private final WaveguideService waveguideService;

    @PostMapping
    @Operation(summary = "Add a layer to a waveguide", description = "Adds a new layer to the specified waveguide")
    public ResponseEntity<LayerResponse> addLayer(
            @PathVariable UUID waveguideId,
            @Valid @RequestBody LayerRequest request,
            @CurrentUser User currentUser
    ) {
        LayerResponse response = waveguideService.addLayer(waveguideId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{layerId}")
    @Operation(summary = "Update a layer", description = "Updates an existing layer in the specified waveguide")
    public ResponseEntity<LayerResponse> updateLayer(
            @PathVariable UUID waveguideId,
            @PathVariable UUID layerId,
            @Valid @RequestBody LayerRequest request,
            @CurrentUser User currentUser
    ) {
        LayerResponse response = waveguideService.updateLayer(waveguideId, layerId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{layerId}")
    @Operation(summary = "Delete a layer", description = "Deletes a layer from the specified waveguide")
    public ResponseEntity<Void> deleteLayer(
            @PathVariable UUID waveguideId,
            @PathVariable UUID layerId,
            @CurrentUser User currentUser
    ) {
        waveguideService.deleteLayer(waveguideId, layerId, currentUser);
        return ResponseEntity.noContent().build();
    }
}