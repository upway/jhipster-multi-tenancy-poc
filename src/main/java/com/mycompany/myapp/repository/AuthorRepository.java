package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Author;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the Author entity.
 */
@Transactional()
public interface AuthorRepository extends JpaRepository<Author,Long> {

}
