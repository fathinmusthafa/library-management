package com.sinaukoding.library.management.controller.managementuser;

import com.sinaukoding.library.management.model.filter.UserFilterRecord;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sinaukoding.library.management.service.managementuser.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;


    @PostMapping("register")
//    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> register(@Valid @RequestBody UserRequestRecord request){
        userService.add(request);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @PostMapping("edit")
    public BaseResponse<?> edit(@Valid @RequestBody UserRequestRecord request) {
        userService.edit(request);
        return BaseResponse.ok("Data berhasil diubah", null);
    }

    @PostMapping("find-all")
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                   @RequestBody UserFilterRecord filterRequest) {
        return BaseResponse.ok(null, userService.findAll(filterRequest, pageable));
    }

    @GetMapping("find-by-id/{id}")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, userService.findById(id));

    }
}
