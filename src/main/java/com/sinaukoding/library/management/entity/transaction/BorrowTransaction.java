package com.sinaukoding.library.management.entity.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sinaukoding.library.management.entity.app.BaseEntity;
import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.model.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_borrow", indexes = {
        @Index(name = "idx_borrow_created_date", columnList = "createdDate"),
        @Index(name = "idx_borow_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_borrow_status", columnList = "status")
})
public class BorrowTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate borrowDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;

    private Integer renewalCount = 0;

    private String notes;

    @OneToOne(mappedBy = "borrowTransaction", cascade = CascadeType.ALL)
    private Fine fine;


}
