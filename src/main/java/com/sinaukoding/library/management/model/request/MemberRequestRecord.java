package com.sinaukoding.library.management.model.request;

import com.sinaukoding.library.management.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberRequestRecord(String id,
                                  @NotBlank(message = "User Id tidak boleh kosong") String userId,
                                  @NotBlank(message = "Phone Number tidak boleh kosong") String phone,
                                  @NotBlank(message = "Address tidak boleh kosong") String address,
                                  @NotNull(message = "Status tidak boleh kosong") Status status) {

}
