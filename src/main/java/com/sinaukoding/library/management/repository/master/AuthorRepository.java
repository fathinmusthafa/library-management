package com.sinaukoding.library.management.repository.master;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.master.Author;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthorRepository extends JpaRepository<Author, String>, JpaSpecificationExecutor<Author> {
    Boolean existsByName(String name);

    Boolean existsByNameAndIdNot( String name, String id);
}
