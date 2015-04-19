package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.Application;
import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AuthorResource REST controller.
 *
 * @see AuthorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AuthorResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_MY_SIZE = 0;
    private static final Integer UPDATED_MY_SIZE = 1;

    private static final Boolean DEFAULT_NEXT_VAL = false;
    private static final Boolean UPDATED_NEXT_VAL = true;

    @Inject
    private AuthorRepository authorRepository;

    private MockMvc restAuthorMockMvc;

    private Author author;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuthorResource authorResource = new AuthorResource();
        ReflectionTestUtils.setField(authorResource, "authorRepository", authorRepository);
        this.restAuthorMockMvc = MockMvcBuilders.standaloneSetup(authorResource).build();
    }

    @Before
    public void initTest() {
        author = new Author();
        author.setName(DEFAULT_NAME);
        author.setMySize(DEFAULT_MY_SIZE);
        author.setNextVal(DEFAULT_NEXT_VAL);
    }

    @Test
    @Transactional
    public void createAuthor() throws Exception {
        // Validate the database is empty
        assertThat(authorRepository.findAll()).hasSize(0);

        // Create the Author
        restAuthorMockMvc.perform(post("/api/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(author)))
                .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(1);
        Author testAuthor = authors.iterator().next();
        assertThat(testAuthor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAuthor.getMySize()).isEqualTo(DEFAULT_MY_SIZE);
        assertThat(testAuthor.getNextVal()).isEqualTo(DEFAULT_NEXT_VAL);
    }

    @Test
    @Transactional
    public void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authors
        restAuthorMockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(author.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].mySize").value(DEFAULT_MY_SIZE))
                .andExpect(jsonPath("$.[0].nextVal").value(DEFAULT_NEXT_VAL.booleanValue()));
    }

    @Test
    @Transactional
    public void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(author.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.mySize").value(DEFAULT_MY_SIZE))
            .andExpect(jsonPath("$.nextVal").value(DEFAULT_NEXT_VAL.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Update the author
        author.setName(UPDATED_NAME);
        author.setMySize(UPDATED_MY_SIZE);
        author.setNextVal(UPDATED_NEXT_VAL);
        restAuthorMockMvc.perform(put("/api/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(author)))
                .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(1);
        Author testAuthor = authors.iterator().next();
        assertThat(testAuthor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAuthor.getMySize()).isEqualTo(UPDATED_MY_SIZE);
        assertThat(testAuthor.getNextVal()).isEqualTo(UPDATED_NEXT_VAL);
    }

    @Test
    @Transactional
    public void deleteAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc.perform(delete("/api/authors/{id}", author.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(0);
    }
}
