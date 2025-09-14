package com.sinaukoding.library.management.service.transaction;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.BorrowTransactionFilterRecord;
import com.sinaukoding.library.management.model.request.BorrowTransactionRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowTransactionService {
    void borrowBook(BorrowTransactionRequestRecord request);
    void returnBook(String transactionId);
    void renewBorrow(String transactionId);
    Page<SimpleMap> findAll(BorrowTransactionFilterRecord filterRequest, Pageable pageable);
    SimpleMap findById(String id);
    Page<SimpleMap> findMemberTransactions(String memberId, Pageable pageable);
}
