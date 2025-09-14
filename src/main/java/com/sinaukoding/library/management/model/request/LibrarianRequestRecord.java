package com.sinaukoding.library.management.model.request;

import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LibrarianRequestRecord(@NotBlank(message = "User Id harus di isi") String userId,
                                     @NotBlank(message = "No Phone harus di isi") String phone,
                                     String address,
                                     @NotNull(message = "Staus harus di isi") Status status) {
}
