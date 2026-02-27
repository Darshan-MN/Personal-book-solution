package com.example.demo;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    public BookController(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/google")
    public GoogleBook searchGoogleBooks(@RequestParam("q") String query,
                                        @RequestParam(value = "maxResults", required = false) Integer maxResults,
                                        @RequestParam(value = "startIndex", required = false) Integer startIndex) {
        return googleBookService.searchBooks(query, maxResults, startIndex);
    }

     @PostMapping("/books/{googleId}")
    public ResponseEntity<Book> addGoogleBook(@PathVariable String googleId) {

        GoogleBook.Item item = googleBookService.getBookById(googleId);

        if (item == null || item.volumeInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        GoogleBook.VolumeInfo info = item.volumeInfo();
        Book book = new Book();
        book.setId(item.id());
        book.setTitle(info.title());
        
        if (info.authors() != null && !info.authors().isEmpty()) {
            book.setAuthor(info.authors().get(0));
        }
        
        book.setPageCount(info.pageCount());

        Book savedBook = bookRepository.save(book);

        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

}
