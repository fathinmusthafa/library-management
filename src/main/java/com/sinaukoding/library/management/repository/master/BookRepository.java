package com.sinaukoding.library.management.repository.master;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.master.Book;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    Boolean existsByIsbn(String isbn);
    Boolean existsByIsbnAndIdNot(String isbn, String id);
    int countByCategoryId(String categoryId);
    int countByAuthorsId(String authorId);

    // Untuk pencarian custom
    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    int countByAuthorId(@Param("authorId") String authorId);
}
