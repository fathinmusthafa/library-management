package com.sinaukoding.library.management.mapper.managementuser;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MemberMapper {

    public Member requestToEntity(MemberRequestRecord request){
        return Member.builder()
                .phone(request.phone())
                .address(request.address())
                .status(request.status())
                .build();
    }
}
