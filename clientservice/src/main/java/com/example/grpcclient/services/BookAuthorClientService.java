package com.example.grpcclient.services;

import com.example.grpcdemo.Author;
import com.example.grpcdemo.Book;
import com.example.grpcdemo.BookAuthServiceGrpc;
import com.example.grpcdemo.Gender;
import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookAuthorClientService {

    @GrpcClient("grpc-demo-service")
    BookAuthServiceGrpc.BookAuthServiceBlockingStub syncClient;

    @GrpcClient("grpc-demo-service")
    BookAuthServiceGrpc.BookAuthServiceStub asyncClient;

    private final static List<Book> books = new ArrayList<>() {
        {
            add(Book.newBuilder().setAuthorId(1).setTitle("Book1").build());
            add(Book.newBuilder().setAuthorId(1).setTitle("Book2").build());
            add(Book.newBuilder().setAuthorId(2).setTitle("Book3").build());
        }
    };

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(Integer authorId){
        Author authorRequest = Author.newBuilder().setId(authorId).build();
        return syncClient.getAuthor(authorRequest).getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(Integer authorId) throws InterruptedException {
        Author authorRequest = Author.newBuilder().setId(authorId).build();
        final CountDownLatch latch = new CountDownLatch(1);
        List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asyncClient.getBooksByAuthor(authorRequest, new StreamObserver<>() {

            @Override // called whenever a new value comes from the observer
            public void onNext(Book book) {
                log.info("A new value has came!");
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.info("Observer Completed");
                latch.countDown();
            }
        });

        log.info("Start waiting");
        boolean await = latch.await(1, TimeUnit.MINUTES);

        log.info("End waiting");
        return await ? response : Collections.emptyList();
    }

    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {

        final Map<String, Map<Descriptors.FieldDescriptor, Object>> response = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<Book> responseObserver = asyncClient.getExpensiveBook(new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                response.put("Result", book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        books.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
        boolean await = latch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.EMPTY_MAP;

    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBookById(Integer bookId) throws InterruptedException {

        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Book> requestObserver = asyncClient.getBookById(new StreamObserver<>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });


        books.forEach(requestObserver::onNext);
        boolean await = latch.await(1, TimeUnit.MINUTES);
        requestObserver.onCompleted();
        return await ? response : Collections.emptyList();
    }

}
