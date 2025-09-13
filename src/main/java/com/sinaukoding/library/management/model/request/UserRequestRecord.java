package com.sinaukoding.library.management.model.request;

import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UserRequestRecord(String id,
                                @NotBlank(message = "Nama tidak boleh kosong")String name,
                                @NotBlank(message = "Username tidak boleh kosong")String username,
                                @NotBlank(message = "Password tidak boleh kosong")String password,
                                @NotBlank(message = "Email tidak boleh kosong")String email,
                                @NotNull(message = "Status tidak boleh kosong")Status status,
                                @NotNull(message = "Role tidak boleh kosong")Role role) {
}
