package com.yucl.learn.demo.hugegraph;

import java.util.ArrayList;
import java.util.List;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Vertex;

public class BatchExample {

    public static void main(String[] args) {
        // If connect failed will throw a exception.
        HugeClient hugeClient = HugeClient.builder("http://192.168.72.137:8080",
                        "hugegraph")
                .build();

        SchemaManager schema = hugeClient.schema();

        schema.propertyKey("name").asText().ifNotExist().create();
        schema.propertyKey("age").asInt().ifNotExist().create();
        schema.propertyKey("lang").asText().ifNotExist().create();
        schema.propertyKey("date").asDate().ifNotExist().create();
        schema.propertyKey("price").asInt().ifNotExist().create();

        schema.vertexLabel("person")
                .properties("name", "age")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.vertexLabel("person")
                .properties("price")
                .nullableKeys("price")
                .append();

        schema.vertexLabel("software")
                .properties("name", "lang", "price")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        schema.indexLabel("softwareByPrice")
                .onV("software").by("price")
                .range()
                .ifNotExist()
                .create();

        schema.edgeLabel("knows")
                .link("person", "person")
                .properties("date")
                .ifNotExist()
                .create();

        schema.edgeLabel("created")
                .link("person", "software")
                .properties("date")
                .ifNotExist()
                .create();

        schema.indexLabel("createdByDate")
                .onE("created").by("date")
                .secondary()
                .ifNotExist()
                .create();

        // get schema object by name
        System.out.println(schema.getPropertyKey("name"));
        System.out.println(schema.getVertexLabel("person"));
        System.out.println(schema.getEdgeLabel("knows"));
        System.out.println(schema.getIndexLabel("createdByDate"));

        // list all schema objects
        System.out.println(schema.getPropertyKeys());
        System.out.println(schema.getVertexLabels());
        System.out.println(schema.getEdgeLabels());
        System.out.println(schema.getIndexLabels());

        GraphManager graph = hugeClient.graph();

        Vertex marko = new Vertex("person").property("name", "marko")
                .property("age", 29);
        Vertex vadas = new Vertex("person").property("name", "vadas")
                .property("age", 27);
        Vertex lop = new Vertex("software").property("name", "lop")
                .property("lang", "java")
                .property("price", 328);
        Vertex josh = new Vertex("person").property("name", "josh")
                .property("age", 32);
        Vertex ripple = new Vertex("software").property("name", "ripple")
                .property("lang", "java")
                .property("price", 199);
        Vertex peter = new Vertex("person").property("name", "peter")
                .property("age", 35);

        Edge markoKnowsVadas = new Edge("knows").source(marko).target(vadas)
                .property("date", "2016-01-10");
        Edge markoKnowsJosh = new Edge("knows").source(marko).target(josh)
                .property("date", "2013-02-20");
        Edge markoCreateLop = new Edge("created").source(marko).target(lop)
                .property("date",
                        "2017-12-10");
        Edge joshCreateRipple = new Edge("created").source(josh).target(ripple)
                .property("date",
                        "2017-12-10");
        Edge joshCreateLop = new Edge("created").source(josh).target(lop)
                .property("date", "2009-11-11");
        Edge peterCreateLop = new Edge("created").source(peter).target(lop)
                .property("date",
                        "2017-03-24");

        List<Vertex> vertices = new ArrayList<>();
        vertices.add(marko);
        vertices.add(vadas);
        vertices.add(lop);
        vertices.add(josh);
        vertices.add(ripple);
        vertices.add(peter);

        List<Edge> edges = new ArrayList<>();
        edges.add(markoKnowsVadas);
        edges.add(markoKnowsJosh);
        edges.add(markoCreateLop);
        edges.add(joshCreateRipple);
        edges.add(joshCreateLop);
        edges.add(peterCreateLop);

        vertices = graph.addVertices(vertices);
        vertices.forEach(vertex -> System.out.println(vertex));

        edges = graph.addEdges(edges, false);
        edges.forEach(edge -> System.out.println(edge));

        hugeClient.close();
    }
}

