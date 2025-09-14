package com.sinaukoding.library.management.controller.transaction;

import com.sinaukoding.library.management.model.filter.BorrowTransactionFilterRecord;
import com.sinaukoding.library.management.model.request.BorrowTransactionRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.transaction.BorrowTransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("transaction")
public class BorrowTransactionController {

    private final BorrowTransactionService borrowTransactionService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("borrow")
    public BaseResponse<?> borrowBook(@Valid @RequestBody BorrowTransactionRequestRecord request) {
        borrowTransactionService.borrowBook(request);
        return BaseResponse.ok("Buku berhasil dipinjam", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("return/{id}")
    public BaseResponse<?> returnBook(@PathVariable String id) {
        borrowTransactionService.returnBook(id);
        return BaseResponse.ok("Buku berhasil dikembalikan", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("renew/{id}")
    public BaseResponse<?> renewBorrow(@PathVariable String id) {
        borrowTransactionService.renewBorrow(id);
        return BaseResponse.ok("Peminjaman berhasil diperpanjang", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("find-all")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "borrowDate,desc"), required = true)
    })
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "borrowDate") Pageable pageable,
                                   @RequestBody BorrowTransactionFilterRecord filterRequest) {
        return BaseResponse.ok(null, borrowTransactionService.findAll(filterRequest, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("find-by-id/{id}")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, borrowTransactionService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("member/{memberId}")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "borrowDate,desc"), required = true)
    })
    public BaseResponse<?> findMemberTransactions(@PathVariable String memberId,
                                                  @PageableDefault(direction = Sort.Direction.DESC, sort = "borrowDate") Pageable pageable) {
        return BaseResponse.ok(null, borrowTransactionService.findMemberTransactions(memberId, pageable));
    }
}
