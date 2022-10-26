package com.example.grpcclient.controllers;

import com.example.grpcclient.services.BookAuthorClientService;
import com.google.protobuf.Descriptors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookAuthorController {

    private final BookAuthorClientService bookAuthorClientService;

    @GetMapping("/author/{authorId}")
    public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable Integer authorId){
        return bookAuthorClientService.getAuthor(authorId);
    }

}
