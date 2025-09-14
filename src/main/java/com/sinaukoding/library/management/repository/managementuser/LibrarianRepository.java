package com.sinaukoding.library.management.repository.managementuser;

import com.sinaukoding.library.management.entity.managementuser.Librarian;
import com.sinaukoding.library.management.entity.managementuser.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, String>, JpaSpecificationExecutor<Librarian> {

    Optional<Librarian> findByUserId(String userId);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, String id);
    boolean existsByUserId(String userId);

}
