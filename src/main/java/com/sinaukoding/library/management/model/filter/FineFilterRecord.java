package com.sinaukoding.library.management.model.filter;


import com.sinaukoding.library.management.model.enums.FineStatus;

import java.time.LocalDateTime;

public record FineFilterRecord(String memberId,
                               FineStatus status,
                               LocalDateTime createdDateFrom,
                               LocalDateTime createdDateTo) {
}
