package com.sinaukoding.library.management.mapper.managementuser;

import com.sinaukoding.library.management.entity.managementuser.Librarian;
import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.model.request.LibrarianRequestRecord;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class LibrarianMapper {

    public Librarian requestToEntity(LibrarianRequestRecord request){
        return Librarian.builder()
                .phone(request.phone())
                .address(request.address())
                .status(request.status())
                .build();
    }
}
