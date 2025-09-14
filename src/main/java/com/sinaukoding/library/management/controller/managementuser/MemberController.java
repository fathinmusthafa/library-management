package com.sinaukoding.library.management.controller.managementuser;

import com.sinaukoding.library.management.model.filter.MemberFilterRecord;
import com.sinaukoding.library.management.model.filter.UserFilterRecord;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.managementuser.MemberService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("save")
    public BaseResponse<?> save(@Valid @RequestBody MemberRequestRecord request) {
        memberService.add(request);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("edit")
    public BaseResponse<?> edit(@Valid @RequestBody MemberRequestRecord request) {
        memberService.edit(request);
        return BaseResponse.ok("Data berhasil diubah", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','MEMBER')")
    @PostMapping("find-all")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "modifiedDate,desc"), required = true)
    })
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                   @Valid @RequestBody MemberFilterRecord filterRequest) {
        return BaseResponse.ok(null, memberService.findAll(filterRequest, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','MEMBER')")
    @GetMapping("find-by-id/{id}")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, memberService.findById(id));

    }
}
