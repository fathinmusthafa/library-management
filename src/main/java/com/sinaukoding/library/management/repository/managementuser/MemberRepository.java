package com.sinaukoding.library.management.repository.managementuser;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.managementuser.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String>, JpaSpecificationExecutor<Member> {

    Optional<Member> UserId(String id);

    Boolean existsByUserId(String id);

    Boolean existsByPhone(String phone);

    Optional<Member> findByUserId(String id);

    Boolean existsByPhoneAndIdNot(String phone, String userId);
}
