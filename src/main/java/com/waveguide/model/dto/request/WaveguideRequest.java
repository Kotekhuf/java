package com.waveguide.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaveguideRequest {

    @NotNull(message = "n_eff_min is required")
    @Min(value = 0, message = "n_eff_min must be greater than 0")
    private Double nEffMin;

    @NotNull(message = "n_eff_max is required")
    @Min(value = 0, message = "n_eff_max must be greater than 0")
    private Double nEffMax;

    @Valid
    private List<LayerRequest> layers;
}