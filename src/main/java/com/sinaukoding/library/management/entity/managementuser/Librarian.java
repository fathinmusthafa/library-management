package com.sinaukoding.library.management.entity.managementuser;

import com.sinaukoding.library.management.entity.app.BaseEntity;
import com.sinaukoding.library.management.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_librarian", indexes = {
        @Index(name = "idx_librarian_created_date", columnList = "createdDate"),
        @Index(name = "idx_librarian_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_librarian_status", columnList = "status")
})
public class Librarian extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AKTIF;
}
