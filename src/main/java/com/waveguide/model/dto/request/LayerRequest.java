package com.waveguide.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerRequest {

    @NotNull(message = "E is required")
    private Double E;

    @NotNull(message = "re_eps is required")
    private Double reEps;

    @NotNull(message = "im_eps is required")
    private Double imEps;

    @NotNull(message = "d is required")
    @Min(value = 0, message = "d must be greater than 0")
    private Double d;
}