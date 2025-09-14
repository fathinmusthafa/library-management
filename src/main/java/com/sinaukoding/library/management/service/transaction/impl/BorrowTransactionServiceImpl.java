package com.sinaukoding.library.management.service.transaction.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.entity.transaction.BorrowTransaction;
import com.sinaukoding.library.management.entity.transaction.Fine;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.enums.BorrowStatus;
import com.sinaukoding.library.management.model.enums.FineStatus;
import com.sinaukoding.library.management.model.filter.BorrowTransactionFilterRecord;
import com.sinaukoding.library.management.model.request.BorrowTransactionRequestRecord;
import com.sinaukoding.library.management.repository.managementuser.MemberRepository;
import com.sinaukoding.library.management.repository.master.BookRepository;
import com.sinaukoding.library.management.repository.transaction.BorrowTransactionRepository;
import com.sinaukoding.library.management.repository.transaction.FineRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.transaction.BorrowTransactionService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowTransactionServiceImpl implements BorrowTransactionService {

    private final BorrowTransactionRepository borrowTransactionRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ValidatorService validatorService;
    private final FineRepository fineRepository;

    @Override
    public void borrowBook(BorrowTransactionRequestRecord request) {
        validatorService.validator(request);

        Member member = memberRepository.findByUserId(request.userId())
                .orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        // Cek available buku
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Buku tidak tersedia untuk dipinjam");
        }

        // Cek buku
        if (borrowTransactionRepository.existsByBookIdAndStatus(book.getId(), BorrowStatus.BORROWED)) {
            throw new RuntimeException("Buku sedang dipinjam oleh member lain");
        }

        // Cek batas peminjaman member (5 hari)
        int activeBorrows = borrowTransactionRepository.countByMemberIdAndStatus(member.getId(), BorrowStatus.BORROWED);
        if (activeBorrows >= 5) {
            throw new RuntimeException("Member telah mencapai batas peminjaman");
        }

        BorrowTransaction transaction = new BorrowTransaction();
        transaction.setMember(member);
        transaction.setBook(book);
        transaction.setBorrowDate(request.borrowDate());
        transaction.setDueDate(request.dueDate());
        transaction.setStatus(BorrowStatus.BORROWED);
        transaction.setNotes(request.notes());
        transaction.setRenewalCount(0);

        // update available buku
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        borrowTransactionRepository.save(transaction);
        log.info("Buku berhasil dipinjam oleh member: {}", member.getId());
    }

    @Override
    public void returnBook(String transactionId) {

        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaksi peminjaman tidak ditemukan"));

        if (transaction.getStatus() != BorrowStatus.BORROWED && transaction.getStatus() != BorrowStatus.OVERDUE) {
            throw new RuntimeException("Buku sudah dikembalikan");
        }

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(BorrowStatus.RETURNED);

        // update available buku
        Book book = transaction.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        // Cek keterlambatan, hitung denda
        if (transaction.getReturnDate().isAfter(transaction.getDueDate())) {
            createOverdueFine(transaction);
        }

        borrowTransactionRepository.save(transaction);
        log.info("Buku berhasil dikembalikan untuk transaksi: {}", transactionId);
    }

    private void createOverdueFine(BorrowTransaction transaction) {
        LocalDate dueDate = transaction.getDueDate();
        LocalDate returnDate = transaction.getReturnDate();

        if (returnDate.isAfter(dueDate)) {
            long daysOverdue = 0;
            LocalDate currentDate = dueDate;

            while (currentDate.isBefore(returnDate)) {
                // Cek jika hari bukan weekend (Senin-Jumat)
                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                        currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    daysOverdue++;
                }
                currentDate = currentDate.plusDays(1);
            }
            // Hitung denda (5000 per hari)
            BigDecimal fineAmount = BigDecimal.valueOf(daysOverdue * 5000);

            Fine fine = new Fine();
            fine.setBorrowTransaction(transaction);
            fine.setAmount(fineAmount);
            fine.setReason("Keterlambatan pengembalian: " + daysOverdue + " hari");
            fine.setStatus(FineStatus.OUTSTANDING);

            fineRepository.save(fine);
            log.info("Denda created for transaction: {}", transaction.getId());
        }
    }

    @Override
    public void renewBorrow(String transactionId) {

        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaksi peminjaman tidak ditemukan"));

        if (transaction.getStatus() != BorrowStatus.BORROWED) {
            throw new RuntimeException("Hanya buku yang sedang dipinjam yang dapat diperpanjang");
        }

        // 2 kali perpanjangan
        if (transaction.getRenewalCount() >= 2) {
            throw new RuntimeException("Batas perpanjangan telah tercapai");
        }

        // Perpanjang 7 hari dari due date sebelumnya
        LocalDate newDueDate = transaction.getDueDate().plusDays(7);
        transaction.setDueDate(newDueDate);
        transaction.setRenewalCount(transaction.getRenewalCount() + 1);

        borrowTransactionRepository.save(transaction);
        log.info("Peminjaman berhasil diperpanjang untuk transaksi: {}", transactionId);
    }

    @Override
    public Page<SimpleMap> findAll(BorrowTransactionFilterRecord filterRequest, Pageable pageable) {
        Page<BorrowTransaction> transactions = borrowTransactionRepository.findByFilters(
                filterRequest.userId(),
                filterRequest.bookId(),
                filterRequest.status(),
                filterRequest.borrowDateFrom(),
                filterRequest.borrowDateTo(),
                filterRequest.dueDateFrom(),
                filterRequest.dueDateTo(),
                pageable
        );

        List<SimpleMap> listData = transactions.stream().map(transaction -> {
            SimpleMap data = new SimpleMap();
            data.put("id", transaction.getId());
            data.put("borrowDate", transaction.getBorrowDate());
            data.put("dueDate", transaction.getDueDate());
            data.put("returnDate", transaction.getReturnDate());
            data.put("status", transaction.getStatus());
            data.put("renewalCount", transaction.getRenewalCount());
            data.put("notes", transaction.getNotes());

            if (transaction.getMember() != null) {
                SimpleMap memberData = new SimpleMap();
                memberData.put("id", transaction.getMember().getId());
                memberData.put("name", transaction.getMember().getUser().getName());
                data.put("member", memberData);
            }

            if (transaction.getBook() != null) {
                SimpleMap bookData = new SimpleMap();
                bookData.put("id", transaction.getBook().getId());
                bookData.put("title", transaction.getBook().getTitle());
                bookData.put("isbn", transaction.getBook().getIsbn());
                data.put("book", bookData);
            }


            if (transaction.getFine() != null) {
                SimpleMap fineData = new SimpleMap();
                fineData.put("id", transaction.getFine().getId());
                fineData.put("amount", transaction.getFine().getAmount());
                fineData.put("status", transaction.getFine().getStatus());
                data.put("fine", fineData);
            }

            return data;
        }).toList();

        return AppPage.create(listData, pageable, transactions.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaksi peminjaman tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", transaction.getId());
        data.put("borrowDate", transaction.getBorrowDate());
        data.put("dueDate", transaction.getDueDate());
        data.put("returnDate", transaction.getReturnDate());
        data.put("status", transaction.getStatus());
        data.put("renewalCount", transaction.getRenewalCount());
        data.put("notes", transaction.getNotes());

        // Member data
        if (transaction.getMember() != null) {
            SimpleMap memberData = new SimpleMap();
            memberData.put("id", transaction.getMember().getId());
            memberData.put("name", transaction.getMember().getUser().getName());
            memberData.put("phone", transaction.getMember().getPhone());
            data.put("member", memberData);
        }

        // Book data
        if (transaction.getBook() != null) {
            SimpleMap bookData = new SimpleMap();
            bookData.put("id", transaction.getBook().getId());
            bookData.put("title", transaction.getBook().getTitle());
            bookData.put("isbn", transaction.getBook().getIsbn());
            bookData.put("publisher", transaction.getBook().getPublisher());
            data.put("book", bookData);
        }

        // Fine
        if (transaction.getFine() != null) {
            SimpleMap fineData = new SimpleMap();
            fineData.put("id", transaction.getFine().getId());
            fineData.put("amount", transaction.getFine().getAmount());
            fineData.put("reason", transaction.getFine().getReason());
            fineData.put("status", transaction.getFine().getStatus());
            fineData.put("paidDate", transaction.getFine().getPaidDate());
            data.put("fine", fineData);
        }

        return data;
    }

    @Override
    public Page<SimpleMap> findMemberTransactions(String memberId, Pageable pageable) {
        BorrowTransactionFilterRecord filter = new BorrowTransactionFilterRecord(
                memberId, null, null, null, null, null, null
        );
        return findAll(filter, pageable);
    }
}
