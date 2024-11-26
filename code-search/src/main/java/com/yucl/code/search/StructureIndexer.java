package com.yucl.code.search;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureIndexer {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "code_structure_index";

    public StructureIndexer(RestHighLevelClient client) {
        this.client = client;
    }

    public void indexStructure(String id, String code) throws IOException {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        Map<String, Object> structureInfo = extractStructureInfo(cu);
        
        IndexRequest request = new IndexRequest(INDEX_NAME)
                .id(id)
                .source(structureInfo, XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Structure index response: " + response.getResult().name());
    }

    private Map<String, Object> extractStructureInfo(CompilationUnit cu) {
        Map<String, Object> structureInfo = new HashMap<>();
        List<Map<String, Object>> classes = new ArrayList<>();
        
        // 提取包信息
        cu.getPackageDeclaration().ifPresent(pkg -> 
            structureInfo.put("package", pkg.getNameAsString()));

        // 提取类信息
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("name", cls.getNameAsString());
            classInfo.put("type", cls.isInterface() ? "interface" : "class");
            
            // 提取继承和实现信息
            if (!cls.getExtendedTypes().isEmpty()) {
                classInfo.put("extends", 
                    cls.getExtendedTypes().stream()
                        .map(t -> t.getNameAsString())
                        .toList());
            }
            if (!cls.getImplementedTypes().isEmpty()) {
                classInfo.put("implements", 
                    cls.getImplementedTypes().stream()
                        .map(t -> t.getNameAsString())
                        .toList());
            }

            // 提取方法信息
            List<Map<String, Object>> methods = new ArrayList<>();
            cls.getMethods().forEach(method -> {
                Map<String, Object> methodInfo = new HashMap<>();
                methodInfo.put("name", method.getNameAsString());
                methodInfo.put("returnType", method.getType().asString());
                methodInfo.put("parameters", 
                    method.getParameters().stream()
                        .map(p -> Map.of(
                            "type", p.getType().asString(),
                            "name", p.getNameAsString()))
                        .toList());
                methods.add(methodInfo);
            });
            classInfo.put("methods", methods);
            
            classes.add(classInfo);
        });

        structureInfo.put("classes", classes);
        return structureInfo;
    }

    public List<SearchResult> search(String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        // 构建结构化查询
        searchSourceBuilder.query(
            QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("package", query))
                .should(QueryBuilders.matchQuery("classes.name", query))
                .should(QueryBuilders.matchQuery("classes.methods.name", query))
                .should(QueryBuilders.matchQuery("classes.methods.returnType", query))
        );
        
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        
        List<SearchResult> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            results.add(new SearchResult(
                    hit.getId(),
                    hit.getScore(),
                    hit.getSourceAsMap()
            ));
        }
        
        return results;
    }

    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}
