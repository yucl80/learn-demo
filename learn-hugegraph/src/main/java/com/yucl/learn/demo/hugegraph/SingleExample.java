package com.yucl.learn.demo.hugegraph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Path;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.gremlin.Result;
import com.baidu.hugegraph.structure.gremlin.ResultSet;

public class SingleExample {

    public static void main(String[] args) throws IOException {
        // If connect failed will throw a exception.
        HugeClient hugeClient = HugeClient.builder("http://192.168.72.137:8080",
                        "hugegraph")
                .build();

        SchemaManager schema = hugeClient.schema();

        schema.propertyKey("name").asText().ifNotExist().create();
        schema.propertyKey("age").asInt().ifNotExist().create();
        schema.propertyKey("city").asText().ifNotExist().create();
        schema.propertyKey("weight").asDouble().ifNotExist().create();
        schema.propertyKey("lang").asText().ifNotExist().create();
        schema.propertyKey("date").asDate().ifNotExist().create();
        schema.propertyKey("price").asInt().ifNotExist().create();

        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.vertexLabel("software")
                .properties("name", "lang", "price")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.indexLabel("personByCity")
                .onV("person")
                .by("city")
                .secondary()
                .ifNotExist()
                .create();

        schema.indexLabel("personByAgeAndCity")
                .onV("person")
                .by("age", "city")
                .secondary()
                .ifNotExist()
                .create();

        schema.indexLabel("softwareByPrice")
                .onV("software")
                .by("price")
                .range()
                .ifNotExist()
                .create();

        schema.edgeLabel("knows")
                .sourceLabel("person")
                .targetLabel("person")
                .properties("date", "weight")
                .ifNotExist()
                .create();

        schema.edgeLabel("created")
                .sourceLabel("person").targetLabel("software")
                .properties("date", "weight")
                .ifNotExist()
                .create();

        schema.indexLabel("createdByDate")
                .onE("created")
                .by("date")
                .secondary()
                .ifNotExist()
                .create();

        schema.indexLabel("createdByWeight")
                .onE("created")
                .by("weight")
                .range()
                .ifNotExist()
                .create();

        schema.indexLabel("knowsByWeight")
                .onE("knows")
                .by("weight")
                .range()
                .ifNotExist()
                .create();

        GraphManager graph = hugeClient.graph();
        Vertex marko = graph.addVertex(T.label, "person", "name", "marko",
                "age", 29, "city", "Beijing");
        Vertex vadas = graph.addVertex(T.label, "person", "name", "vadas",
                "age", 27, "city", "Hongkong");
        Vertex lop = graph.addVertex(T.label, "software", "name", "lop",
                "lang", "java", "price", 328);
        Vertex josh = graph.addVertex(T.label, "person", "name", "josh",
                "age", 32, "city", "Beijing");
        Vertex ripple = graph.addVertex(T.label, "software", "name", "ripple",
                "lang", "java", "price", 199);
        Vertex peter = graph.addVertex(T.label, "person", "name", "peter",
                "age", 35, "city", "Shanghai");

        marko.addEdge("knows", vadas, "date", "2016-01-10", "weight", 0.5);
        marko.addEdge("knows", josh, "date", "2013-02-20", "weight", 1.0);
        marko.addEdge("created", lop, "date", "2017-12-10", "weight", 0.4);
        josh.addEdge("created", lop, "date", "2009-11-11", "weight", 0.4);
        josh.addEdge("created", ripple, "date", "2017-12-10", "weight", 1.0);
        peter.addEdge("created", lop, "date", "2017-03-24", "weight", 0.2);

        GremlinManager gremlin = hugeClient.gremlin();
        System.out.println("==== Path ====");
        ResultSet resultSet = gremlin.gremlin("g.V().outE().path()").execute();
        Iterator<Result> results = resultSet.iterator();
        results.forEachRemaining(result -> {
            System.out.println(result.getObject().getClass());
            Object object = result.getObject();
            if (object instanceof Vertex) {
                System.out.println(((Vertex) object).id());
            } else if (object instanceof Edge) {
                System.out.println(((Edge) object).id());
            } else if (object instanceof Path) {
                List<Object> elements = ((Path) object).objects();
                elements.forEach(element -> {
                    System.out.println(element.getClass());
                    System.out.println(element);
                });
            } else {
                System.out.println(object);
            }
        });

        hugeClient.close();
    }
}

