package com.example.app;

import com.example.grpcdemo.Author;
import com.example.grpcdemo.Book;
import com.example.grpcdemo.BookAuthServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Slf4j
public class BookAuthorService extends BookAuthServiceGrpc.BookAuthServiceImplBase {

    private final static List<Author> authors = new ArrayList<>() {
        {
            add(Author.newBuilder().setId(1).setName("Ahmed").build());
            add(Author.newBuilder().setId(2).setName("Aly").build());
            add(Author.newBuilder().setId(3).setName("Mohamed").build());
        }
    };

    private final static List<Book> books = new ArrayList<>() {
        {
            add(Book.newBuilder().setAuthorId(1).setTitle("Book1").build());
            add(Book.newBuilder().setAuthorId(1).setTitle("Book2").build());
            add(Book.newBuilder().setAuthorId(2).setTitle("Book3").build());
        }
    };

    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        authors.stream()
                .filter(a -> a.getId() == request.getId())
                .findFirst()
                .ifPresent(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
        books.stream()
                .filter(b -> b.getAuthorId() == request.getId())
                .forEach(responseObserver::onNext);

        log.info("Response Completed");
        responseObserver.onCompleted();
    }
}
