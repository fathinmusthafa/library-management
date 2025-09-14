package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FineRequestRecord(@NotBlank(message = "ID harus di isi") String borrowTransactionId,
                                @NotNull(message = "Jml Denda harus di isi")
                                @DecimalMin(value = "0.0", inclusive = false,
                                message = "Jumlah denda harus lebih dari 0")
                                BigDecimal amount,
                                String reason) {
}
