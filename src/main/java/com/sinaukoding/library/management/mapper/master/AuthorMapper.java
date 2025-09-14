package com.sinaukoding.library.management.mapper.master;

import com.sinaukoding.library.management.entity.master.Author;
import com.sinaukoding.library.management.model.request.AuthorRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author requestToEntity(AuthorRequestRecord request){
        return Author.builder()
                .name(request.name())
                .biography(request.biography())
                .birthDate(request.birthDate())
                .build();

    }
}
