package com.waveguide.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerResponse {
    
    private UUID id;
    private Integer layerIndex;
    private Double E;
    private Double reEps;
    private Double imEps;
    private Double d;
}