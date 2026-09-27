package com.hsbm.backend;

import jakarta.validation.constraints.*;

// ServiceItem.java : 주고받는 데이터 모양 (Vue의 ServiceItem과 같음)
public record ServiceItem(
    Long id,
    @NotBlank String name,
    @NotBlank String category,
    @NotNull @Positive Integer price
) {}