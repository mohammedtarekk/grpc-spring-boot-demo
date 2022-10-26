package com.example.grpcclient.services;

import com.example.grpcdemo.Author;
import com.example.grpcdemo.BookAuthServiceGrpc;
import com.google.protobuf.Descriptors;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BookAuthorClientService {

    @GrpcClient("grpc-demo-service")
    BookAuthServiceGrpc.BookAuthServiceBlockingStub syncClient;

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(Integer authorId){
        Author authorRequest = Author.newBuilder().setId(authorId).build();
        return syncClient.getAuthor(authorRequest).getAllFields();
    }

}
