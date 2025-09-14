package com.sinaukoding.library.management.entity.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sinaukoding.library.management.entity.app.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_author", indexes = {
        @Index(name = "idx_author_created_date", columnList = "createdDate"),
        @Index(name = "idx_author_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_author_name", columnList = "name")
})
public class Author extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String biography;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private List<Book> books;
}
