package com.example.grpcclient.services;

import com.example.grpcdemo.Author;
import com.example.grpcdemo.Book;
import com.example.grpcdemo.BookAuthServiceGrpc;
import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookAuthorClientService {

    @GrpcClient("grpc-demo-service")
    BookAuthServiceGrpc.BookAuthServiceBlockingStub syncClient;

    @GrpcClient("grpc-demo-service")
    BookAuthServiceGrpc.BookAuthServiceStub asyncClient;

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

}
