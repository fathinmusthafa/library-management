package com.sinaukoding.library.management.entity.managementuser;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sinaukoding.library.management.entity.app.BaseEntity;
import com.sinaukoding.library.management.entity.transaction.BorrowTransaction;
import com.sinaukoding.library.management.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_member", indexes = {
        @Index(name = "idx_member_created_date", columnList = "createdDate"),
        @Index(name = "idx_member_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_member_user_id", columnList = "user_id"),
        @Index(name = "idx_member_status", columnList = "status")
})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String phone;

    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate membershipDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.AKTIF;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowTransaction> borrowTransactions;
}
