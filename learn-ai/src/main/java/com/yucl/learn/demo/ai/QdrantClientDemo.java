package com.yucl.learn.demo.ai;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class QdrantClientDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(
                                "localhost",
                                6334,
                                false
                        )
//                        .withApiKey("FEg7NEaqJe14DYxmPmo0h60p-I_Dw8L9LSsbubipGpKJymSG9Kr0VA")
                        .build()
        );



        List<String> collections = client.listCollectionsAsync(Duration.ofSeconds(5)).get();

        collections.forEach(str->{
            System.out.println(str);
        });
    }
}
