package com.sinaukoding.library.management.controller.managementuser;

import com.sinaukoding.library.management.model.filter.LibrarianFilterRecord;
import com.sinaukoding.library.management.model.request.LibrarianRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.managementuser.LibrarianService;
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
@RequestMapping("librarian")
public class LibrarianController {

    private final LibrarianService librarianService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("save")
    public BaseResponse<?> save(@Valid @RequestBody LibrarianRequestRecord request) {
        librarianService.add(request);
        return BaseResponse.ok("Data librarian berhasil disimpan", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("edit")
    public BaseResponse<?> edit(@Valid @RequestBody LibrarianRequestRecord request) {
        librarianService.edit(request);
        return BaseResponse.ok("Data librarian berhasil diubah", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("delete/{id}")
    public BaseResponse<?> delete(@PathVariable String id) {
        librarianService.delete(id);
        return BaseResponse.ok("Data librarian berhasil dihapus", null);
    }


    @PostMapping("find-all")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "createdDate,desc"), required = true)
    })
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "createdDate") Pageable pageable,
                                   @RequestBody LibrarianFilterRecord filterRequest) {
        return BaseResponse.ok(null, librarianService.findAll(filterRequest, pageable));
    }



    @GetMapping("find-by-user-id/{userId}")
    public BaseResponse<?> findByUserId(@PathVariable String userId) {
        return BaseResponse.ok(null, librarianService.findByUserId(userId));
    }
}
