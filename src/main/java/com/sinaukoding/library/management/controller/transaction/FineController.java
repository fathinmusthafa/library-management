package com.sinaukoding.library.management.controller.transaction;


import com.sinaukoding.library.management.model.filter.FineFilterRecord;
import com.sinaukoding.library.management.model.request.FineRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.transaction.FineService;
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
@RequestMapping("fine")
public class FineController {

    private final FineService fineService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("create")
    public BaseResponse<?> createFine(@Valid @RequestBody FineRequestRecord request) {
        fineService.createFine(request);
        return BaseResponse.ok("Denda berhasil dibuat", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("pay/{id}")
    public BaseResponse<?> payFine(@PathVariable String id) {
        fineService.payFine(id);
        return BaseResponse.ok("Denda berhasil dibayar", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("waive/{id}")
    public BaseResponse<?> waiveFine(@PathVariable String id) {
        fineService.waiveFine(id);
        return BaseResponse.ok("Denda berhasil dibebaskan", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("find-all")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "createdDate,desc"), required = true)
    })
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "createdDate") Pageable pageable,
                                   @RequestBody FineFilterRecord filterRequest) {
        return BaseResponse.ok(null, fineService.findAll(filterRequest, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("find-by-id/{id}")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, fineService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("member/{memberId}")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "createdDate,desc"), required = true)
    })
    public BaseResponse<?> findMemberFines(@PathVariable String memberId,
                                           @PageableDefault(direction = Sort.Direction.DESC, sort = "createdDate") Pageable pageable) {
        return BaseResponse.ok(null, fineService.findMemberFines(memberId, pageable));
    }
}
