package com.yucl.code.search;

import java.util.HashMap;
import java.util.Map;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * 为了进一步增强搜索效果，可以结合文件级别和函数级别的嵌入。在这种策略下，可以先对整个文件进行嵌入来捕捉全局上下文，再对每个函数进行细粒度嵌入，最后在搜索时结合两者的信息。
 */
public class MultiLevelEmbedding {

    private Map<String, String> fileEmbeddings = new HashMap<>();
    private Map<String, String> functionEmbeddings = new HashMap<>();

    // 生成文件级别的嵌入
    public void generateFileEmbedding(String filePath, CompilationUnit cu) {
        String fileContent = cu.toString();
        // 假设有生成嵌入的方法
        // String embedding = generateEmbedding(fileContent);
        fileEmbeddings.put(filePath, fileContent);
    }

    // 生成函数级别的嵌入
    public void generateFunctionEmbeddings(String filePath, CompilationUnit cu) {
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            String functionName = method.getNameAsString();
            String embeddingWithContext = new FunctionEmbeddingWithContext().generateFunctionEmbeddingWithContext(method, cu);
            functionEmbeddings.put(filePath + "::" + functionName, embeddingWithContext);
        });
    }

    // 在搜索时结合两者信息
    public void search(String query) {
        // 假设有一个搜索方法可以接受文件和函数级别的嵌入
        for (String filePath : fileEmbeddings.keySet()) {
            String fileEmbedding = fileEmbeddings.get(filePath);
            // 搜索文件级别的嵌入
            // List<String> results = searchEmbedding(query, fileEmbedding);

            // 搜索函数级别的嵌入
            functionEmbeddings.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(filePath))
                    .forEach(entry -> {
                        String functionEmbedding = entry.getValue();
                        // List<String> functionResults = searchEmbedding(query, functionEmbedding);
                        // 合并结果
                    });
        }
    }
}
