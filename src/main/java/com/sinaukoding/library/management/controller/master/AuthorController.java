package com.sinaukoding.library.management.controller.master;

import com.sinaukoding.library.management.model.filter.AuthorFilterRecord;
import com.sinaukoding.library.management.model.filter.CategoryFilterRecord;
import com.sinaukoding.library.management.model.request.AuthorRequestRecord;
import com.sinaukoding.library.management.model.request.CategoryRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.master.AuthorService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("author")
public class AuthorController {

    private final AuthorService authorService;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("save")
    public BaseResponse<?> save(@Valid @RequestBody AuthorRequestRecord request) {
        authorService.add(request);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("edit")
    public BaseResponse<?> edit(@Valid @RequestBody AuthorRequestRecord request) {
        authorService.edit(request);
        return BaseResponse.ok("Data berhasil diubah", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN', 'MEMBER')")
    @PostMapping("find-all")
    @Parameters({
            @Parameter(name = "page", description = "Page Number", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"), required = true),
            @Parameter(name = "size", description = "Size Per Page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"), required = true),
            @Parameter(name = "sort", description = "Sorting Data", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "modifiedDate,desc"), required = true)
    })
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                  @Valid @RequestBody AuthorFilterRecord filterRequest) {
        return BaseResponse.ok(null, authorService.findAll(filterRequest, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @GetMapping("find-by-id/{id}")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, authorService.findById(id));

    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("{authorId}/add-book/{bookId}")
    public BaseResponse<?> addBookToAuthor(@PathVariable String authorId, @PathVariable String bookId) {
        authorService.addBookToAuthor(authorId, bookId);
        return BaseResponse.ok("Buku berhasil ditambahkan ke author", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("{authorId}/add-books")
    public BaseResponse<?> addBooksToAuthor(@PathVariable String authorId, @RequestBody List<String> bookIds) {
        authorService.addBooksToAuthor(authorId, bookIds);
        return BaseResponse.ok("Buku-buku berhasil ditambahkan ke author", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("{authorId}/remove-book/{bookId}")
    public BaseResponse<?> removeBookFromAuthor(@PathVariable String authorId, @PathVariable String bookId) {
        authorService.removeBookFromAuthor(authorId, bookId);
        return BaseResponse.ok("Buku berhasil dihapus dari author", null);
    }



}
