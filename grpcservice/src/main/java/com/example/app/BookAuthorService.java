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
            add(Book.newBuilder().setId(1).setAuthorId(1).setTitle("Book1").build());
            add(Book.newBuilder().setId(2).setAuthorId(1).setTitle("Book2").build());
            add(Book.newBuilder().setId(3).setAuthorId(2).setTitle("Book3").build());
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

    @Override
    public StreamObserver<Book> getExpensiveBook(StreamObserver<Book> responseObserver) {
        return new StreamObserver<>() {

            Book expensiveBook;
            float maxPrice = 0;
            @Override
            public void onNext(Book book) {
                if(book.getPrice() > maxPrice){
                    maxPrice = book.getPrice();
                    expensiveBook = book;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(expensiveBook);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Book> getBooksByIds(StreamObserver<Book> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                books.stream()
                        .filter(b -> b.getId() == book.getId())
                        .findFirst()
                        .ifPresent(responseObserver::onNext);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
