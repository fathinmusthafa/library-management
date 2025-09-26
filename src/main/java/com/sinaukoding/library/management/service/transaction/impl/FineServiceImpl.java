package com.sinaukoding.library.management.service.transaction.impl;

import com.sinaukoding.library.management.entity.transaction.BorrowTransaction;
import com.sinaukoding.library.management.entity.transaction.Fine;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.enums.FineStatus;
import com.sinaukoding.library.management.model.filter.FineFilterRecord;
import com.sinaukoding.library.management.model.request.FineRequestRecord;
import com.sinaukoding.library.management.repository.transaction.BorrowTransactionRepository;
import com.sinaukoding.library.management.repository.transaction.FineRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.transaction.FineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final BorrowTransactionRepository borrowTransactionRepository;
    private final ValidatorService validatorService;

    @Override
    public void createFine(FineRequestRecord request) {
        validatorService.validator(request);

        BorrowTransaction transaction = borrowTransactionRepository.findById(request.borrowTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaksi peminjaman tidak ditemukan"));

        // Cek denda
        if (fineRepository.findByBorrowTransactionId(request.borrowTransactionId()).isPresent()) {
            throw new RuntimeException("Sudah ada denda untuk transaksi ini");
        }

        Fine fine = new Fine();
        fine.setBorrowTransaction(transaction);
        fine.setAmount(request.amount());
        fine.setReason(request.reason());
        fine.setStatus(FineStatus.OUTSTANDING);

        fineRepository.save(fine);
        log.info("Denda created for transaction: {}", request.borrowTransactionId());
    }

    @Override
    public void payFine(String fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Denda tidak ditemukan"));

        if (fine.getStatus() != FineStatus.OUTSTANDING) {
            throw new RuntimeException("Denda sudah dibayar atau dibebaskan");
        }

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDateTime.now());

        fineRepository.save(fine);
        log.info("Denda paid: {}", fineId);

    }

    @Override
    public void waiveFine(String fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Denda tidak ditemukan"));

        if (fine.getStatus() != FineStatus.OUTSTANDING) {
            throw new RuntimeException("Hanya denda outstanding yang dapat diwaive");
        }

        fine.setStatus(FineStatus.WAIVED);

        fineRepository.save(fine);
        log.info("Denda waived: {}", fineId);
    }

    @Override
    public Page<SimpleMap> findAll(FineFilterRecord filterRequest, Pageable pageable) {
        Page<Fine> fines = fineRepository.findByFilters(
                filterRequest.memberId(),
                filterRequest.status(),
                pageable
        );

        List<SimpleMap> listData = fines.stream().map(fine -> {
            SimpleMap data = new SimpleMap();
            data.put("id", fine.getId());
            data.put("amount", fine.getAmount());
            data.put("reason", fine.getReason());
            data.put("status", fine.getStatus());
            data.put("paidDate", fine.getPaidDate());
            data.put("createdDate", fine.getCreatedDate());

            if (fine.getBorrowTransaction() != null) {
                SimpleMap transactionData = new SimpleMap();
                transactionData.put("id", fine.getBorrowTransaction().getId());
                transactionData.put("borrowDate", fine.getBorrowTransaction().getBorrowDate());
                transactionData.put("dueDate", fine.getBorrowTransaction().getDueDate());

                if (fine.getBorrowTransaction().getMember() != null) {
                    SimpleMap memberData = new SimpleMap();
                    memberData.put("id", fine.getBorrowTransaction().getMember().getId());
                    memberData.put("name", fine.getBorrowTransaction().getMember().getUser().getName());
                    transactionData.put("member", memberData);
                }

                data.put("borrowTransaction", transactionData);
            }

            return data;
        }).toList();

        return AppPage.create(listData, pageable, fines.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denda tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", fine.getId());
        data.put("amount", fine.getAmount());
        data.put("reason", fine.getReason());
        data.put("status", fine.getStatus());
        data.put("paidDate", fine.getPaidDate());
        data.put("createdDate", fine.getCreatedDate());
        data.put("modifiedDate", fine.getModifiedDate());

        if (fine.getBorrowTransaction() != null) {
            SimpleMap transactionData = new SimpleMap();
            transactionData.put("id", fine.getBorrowTransaction().getId());
            transactionData.put("borrowDate", fine.getBorrowTransaction().getBorrowDate());
            transactionData.put("dueDate", fine.getBorrowTransaction().getDueDate());
            transactionData.put("returnDate", fine.getBorrowTransaction().getReturnDate());
            transactionData.put("status", fine.getBorrowTransaction().getStatus());

            if (fine.getBorrowTransaction().getMember() != null) {
                SimpleMap memberData = new SimpleMap();
                memberData.put("id", fine.getBorrowTransaction().getMember().getId());
                memberData.put("name", fine.getBorrowTransaction().getMember().getUser().getName());
                memberData.put("phone", fine.getBorrowTransaction().getMember().getPhone());
                transactionData.put("member", memberData);
            }

            if (fine.getBorrowTransaction().getBook() != null) {
                SimpleMap bookData = new SimpleMap();
                bookData.put("id", fine.getBorrowTransaction().getBook().getId());
                bookData.put("title", fine.getBorrowTransaction().getBook().getTitle());
                bookData.put("isbn", fine.getBorrowTransaction().getBook().getIsbn());
                transactionData.put("book", bookData);
            }

            data.put("borrowTransaction", transactionData);
        }

        return data;
    }

    @Override
    public Page<SimpleMap> findMemberFines(String memberId, Pageable pageable) {
        FineFilterRecord filter = new FineFilterRecord(
                memberId, null
        );
        return findAll(filter, pageable);
    }
}
