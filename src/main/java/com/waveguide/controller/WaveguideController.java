package com.waveguide.controller;

import com.waveguide.model.dto.request.WaveguideRequest;
import com.waveguide.model.dto.response.PageResponse;
import com.waveguide.model.dto.response.WaveguideResponse;
import com.waveguide.model.entity.User;
import com.waveguide.security.CurrentUser;
import com.waveguide.service.WaveguideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/waveguides")
@RequiredArgsConstructor
@Tag(name = "Waveguides", description = "Operations for managing waveguides")
public class WaveguideController {

    private final WaveguideService waveguideService;

    @PostMapping
    @Operation(summary = "Create a new waveguide", description = "Creates a new waveguide with the provided parameters")
    public ResponseEntity<WaveguideResponse> createWaveguide(
            @Valid @RequestBody WaveguideRequest request,
            @CurrentUser User currentUser
    ) {
        WaveguideResponse response = waveguideService.createWaveguide(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all waveguides", description = "Returns a paginated list of user's waveguides")
    public ResponseEntity<PageResponse<WaveguideResponse>> getWaveguides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @CurrentUser User currentUser
    ) {
        // Limit page size to 100
        size = Math.min(size, 100);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<WaveguideResponse> waveguidesPage = waveguideService.getWaveguides(currentUser, pageable);
        
        return ResponseEntity.ok(PageResponse.from(waveguidesPage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get waveguide by ID", description = "Returns a specific waveguide by ID")
    public ResponseEntity<WaveguideResponse> getWaveguide(
            @PathVariable("id") UUID waveguideId,
            @CurrentUser User currentUser
    ) {
        WaveguideResponse response = waveguideService.getWaveguideById(waveguideId, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete waveguide", description = "Deletes a specific waveguide by ID")
    public ResponseEntity<Void> deleteWaveguide(
            @PathVariable("id") UUID waveguideId,
            @CurrentUser User currentUser
    ) {
        waveguideService.deleteWaveguide(waveguideId, currentUser);
        return ResponseEntity.noContent().build();
    }
}