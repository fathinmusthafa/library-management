package com.sinaukoding.library.management.entity.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sinaukoding.library.management.entity.app.BaseEntity;
import com.sinaukoding.library.management.model.enums.FineStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_fine", indexes = {
        @Index(name = "idx_fine_created_date", columnList = "createdDate"),
        @Index(name = "idx_fine_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_fine_status", columnList = "status")
})
public class Fine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "borrow_transaction_id", nullable = false)
    private BorrowTransaction borrowTransaction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status = FineStatus.OUTSTANDING;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime paidDate;
}
