package com.sinaukoding.library.management.repository.transaction;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.transaction.BorrowTransaction;
import com.sinaukoding.library.management.model.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, String>, JpaSpecificationExecutor<BorrowTransaction> {

    List<BorrowTransaction> findByMemberIdAndStatus(String memberId, BorrowStatus status);
    List<BorrowTransaction> findByBookIdAndStatus(String bookId, BorrowStatus status);
    Optional<BorrowTransaction> findByIdAndMemberId(String id, String memberId);
    int countByMemberIdAndStatus(String memberId, BorrowStatus status);
    boolean existsByBookIdAndStatus(String bookId, BorrowStatus status);
    List<BorrowTransaction> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate);
    List<BorrowTransaction> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    List<BorrowTransaction> findByStatus(BorrowStatus status);
    List<BorrowTransaction> findByMemberId(String memberId);
    List<BorrowTransaction> findByBookId(String bookId);

    @Query("SELECT bt FROM BorrowTransaction bt WHERE " +
            "(:userId IS NULL OR bt.member.id = :userId) AND " +
            "(:bookId IS NULL OR bt.book.id = :bookId) AND " +
            "(:status IS NULL OR bt.status = :status) AND " +
            "(:borrowDateFrom IS NULL OR bt.borrowDate >= :borrowDateFrom) AND " +
            "(:borrowDateTo IS NULL OR bt.borrowDate <= :borrowDateTo) AND " +
            "(:dueDateFrom IS NULL OR bt.dueDate >= :dueDateFrom) AND " +
            "(:dueDateTo IS NULL OR bt.dueDate <= :dueDateTo)")
    Page<BorrowTransaction> findByFilters(
            @Param("userId") String userId,
            @Param("bookId") String bookId,
            @Param("status") BorrowStatus status,
            @Param("borrowDateFrom") LocalDate borrowDateFrom,
            @Param("borrowDateTo") LocalDate borrowDateTo,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable);
}
