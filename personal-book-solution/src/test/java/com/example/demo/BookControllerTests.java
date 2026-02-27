package com.example.demo;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTests {

    private static MockWebServer mockWebServer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("google.books.base-url", () -> mockWebServer.url("/").toString());
    }

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        bookRepository.save(new Book("lRtdEAAAQBAJ", "Spring in Action", "Craig Walls"));
        bookRepository.save(new Book("12muzgEACAAJ", "Effective Java", "Joshua Bloch"));
    }

    @Test
    void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Spring in Action"))
            .andExpect(jsonPath("$[1].title").value("Effective Java"));
    }

    @Test
    void testAddGoogleBook_HappyPath() throws Exception {
        String googleId = "f779EAAAQBAJ";

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                    {
                      "id": "f779EAAAQBAJ",
                      "volumeInfo": {
                        "title": "Effective Java",
                        "authors": ["Joshua Bloch"],
                        "pageCount": 412
                      }
                    }
                    """)
                .addHeader("Content-Type", "application/json"));
                
        mockMvc.perform(post("/books/" + googleId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(googleId))
                .andExpect(jsonPath("$.title").value("Effective Java"));
                assertThat(bookRepository.findById(googleId)).isPresent();
    }

    @Test
    void testAddGoogleBook_InvalidId_ReturnsError() throws Exception {
        String invalidId = "invalidId";
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockMvc.perform(post("/books/" + invalidId))
                .andExpect(status().isNotFound());
        assertThat(bookRepository.findById(invalidId)).isEmpty();
    }
}
