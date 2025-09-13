package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestRecord(@NotBlank String username,
                                 @NotBlank String password) {
}
