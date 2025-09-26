package com.sinaukoding.library.management.repository.transaction;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.transaction.Fine;
import com.sinaukoding.library.management.model.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, String>, JpaSpecificationExecutor<Fine> {

    List<Fine> findByBorrowTransactionMemberId(String memberId);
    List<Fine> findByStatus(FineStatus status);
    Optional<Fine> findByBorrowTransactionId(String borrowTransactionId);

    @Query("SELECT f FROM Fine f WHERE " +
            "(:memberId IS NULL OR f.borrowTransaction.member.id = :memberId) AND " +
            "(:status IS NULL OR f.status = :status)")
    Page<Fine> findByFilters(
            @Param("memberId") String memberId,
            @Param("status") FineStatus status,
            Pageable pageable);

}
