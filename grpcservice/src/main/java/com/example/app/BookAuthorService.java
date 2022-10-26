package com.example.app;

import com.example.grpcdemo.Author;
import com.example.grpcdemo.BookAuthServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class BookAuthorService extends BookAuthServiceGrpc.BookAuthServiceImplBase {

    private final static List<Author> authors = new ArrayList<>() {
        {
            add(Author.newBuilder().setId(1).setName("Ahmed").build());
            add(Author.newBuilder().setId(2).setName("Aly").build());
            add(Author.newBuilder().setId(3).setName("Mohamed").build());
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
    
}
