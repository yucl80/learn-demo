package com.yucl.learn.demo.ai;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points;

import static io.qdrant.client.ConditionFactory.matchKeyword;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.QueryPoints;

import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

public class QdrantClientDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(
                                "192.168.85.130",
                                6334,
                                false
                        )
//                        .withApiKey("FEg7NEaqJe14DYxmPmo0h60p-I_Dw8L9LSsbubipGpKJymSG9Kr0VA")
                        .build()
        );


        client.createCollectionAsync("test_a",
                VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(4).build()).get();

        List<String> collections = client.listCollectionsAsync(Duration.ofSeconds(5)).get();

        collections.forEach(System.out::println);

        Points.UpdateResult operationInfo =
                client
                        .upsertAsync(
                                "test_a",
                                List.of(
                                        Points.PointStruct.newBuilder()
                                                .setId(id(1))
                                                .setVectors(vectors(0.05f, 0.61f, 0.76f, 0.74f))
                                                .putAllPayload(Map.of("city", value("Berlin")))
                                                .build(),
                                        Points.PointStruct.newBuilder()
                                                .setId(id(2))
                                                .setVectors(vectors(0.19f, 0.81f, 0.75f, 0.11f))
                                                .putAllPayload(Map.of("city", value("London")))
                                                .build(),
                                        Points.PointStruct.newBuilder()
                                                .setId(id(3))
                                                .setVectors(vectors(0.36f, 0.55f, 0.47f, 0.94f))
                                                .putAllPayload(Map.of("city", value("Moscow")))
                                                .build()))
                        // Truncated
                        .get();

        System.out.println(operationInfo);

        List<ScoredPoint> searchResult =
                client.queryAsync(QueryPoints.newBuilder()
                        .setCollectionName("test_a")
                        .setLimit(3)
                        .setQuery(nearest(0.2f, 0.1f, 0.9f, 0.7f))
                        .build()).get();

        System.out.println(searchResult);

        List<ScoredPoint> searchResult2 =
                client.queryAsync(QueryPoints.newBuilder()
                        .setCollectionName("test_a")
                        .setLimit(3)
                        .setFilter(Points.Filter.newBuilder().addMust(matchKeyword("city", "London")))
                        .setQuery(nearest(0.2f, 0.1f, 0.9f, 0.7f))
                        .setWithPayload(enable(true))
                        .build()).get();

        System.out.println(searchResult2);
    }
}
