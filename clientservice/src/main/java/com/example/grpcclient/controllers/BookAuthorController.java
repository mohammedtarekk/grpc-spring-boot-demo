package com.example.grpcclient.controllers;

import com.example.grpcclient.services.BookAuthorClientService;
import com.google.protobuf.Descriptors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookAuthorController {

    private final BookAuthorClientService bookAuthorClientService;

    @GetMapping("/author/{authorId}")
    public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable Integer authorId){
        return bookAuthorClientService.getAuthor(authorId);
    }

    @GetMapping("/books/{authorId}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(@PathVariable Integer authorId) throws InterruptedException {
        return bookAuthorClientService.getBooksByAuthor(authorId);
    }

    @GetMapping("/expensivebook")
    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
        return bookAuthorClientService.getExpensiveBook();
    }

    @GetMapping("books")
    public List<Map<Descriptors.FieldDescriptor, Object>> getBookById(@RequestParam List<Integer> booksIds) throws InterruptedException {
        return bookAuthorClientService.getBooksByIds(booksIds);
    }

}
