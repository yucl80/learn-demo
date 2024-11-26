package com.yucl.code.search;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class SmartCodeEmbeddingProcessor {

    private static final int MAX_CONTEXT_SIZE = 4096; // Adjust based on LLM's context size
    private static final int OVERLAP_SIZE = 512; // Overlap size for splitting
    private static final String openAIKey = "YOUR_OPENAI_KEY";

    // Process a single file and generate embeddings
    public void processFile(String filePath) throws Exception {
        CompilationUnit cu =new JavaParser().parse(new File(filePath)).getResult().get();
        List<MethodDeclaration> methods = new ArrayList<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                super.visit(md, arg);
                methods.add(md);
            }
        }, null);

        for (MethodDeclaration method : methods) {
            String methodCode = method.toString();
            List<String> blocks = splitMethodIntoBlocks(method);
            for (String block : blocks) {
                if (block.length() <= MAX_CONTEXT_SIZE) {
                    System.out.println("Embedding for block in function: " + method.getName());
                    List<float[]> embeddings = generateEmbeddings(block);
                    storeEmbeddings(method.getName(), embeddings);
                } else {
                    List<String> chunks = splitBlockIntoChunks(block);
                    for (String chunk : chunks) {
                        List<float[]> embeddings = generateEmbeddings(chunk);
                        storeEmbeddings(method.getName(), embeddings);
                    }
                }
            }
        }
    }

    // Split a method into blocks based on its structure
    private List<String> splitMethodIntoBlocks(MethodDeclaration method) {
        List<String> blocks = new ArrayList<>();
        method.getBody().ifPresent(body -> {
            List<Statement> statements = body.getStatements();
            StringBuilder block = new StringBuilder();
            for (Statement statement : statements) {
                block.append(statement.toString());
                if (statement instanceof BlockStmt || statement instanceof IfStmt ||
                        statement instanceof ForStmt || statement instanceof WhileStmt) {
                    blocks.add(block.toString());
                    block.setLength(0);
                }
            }
            if (block.length() > 0) {
                blocks.add(block.toString());
            }
        });
        return blocks;
    }

    // Split a large block into chunks with overlapping
    private List<String> splitBlockIntoChunks(String block) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < block.length()) {
            int end = Math.min(start + MAX_CONTEXT_SIZE, block.length());
            String chunk = block.substring(start, end);
            chunks.add(chunk);

            start = end - OVERLAP_SIZE; // Move start pointer back for overlap
        }

        return chunks;
    }

    private List<float[]> generateEmbeddings(String code) {
        List<float[]> embeddings = new ArrayList<>();
        try {
            // 使用SemanticIndexer生成嵌入
            SemanticIndexer semanticIndexer = new SemanticIndexer(openAIKey, "http://localhost:8000");
            float[] embedding = semanticIndexer.generateSemanticEmbedding(code);
            embeddings.add(embedding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return embeddings;
    }

    private void storeEmbeddings(String blockId, List<float[]> embeddings) {
        try {
            // 存储嵌入到ChromaDB
            for (float[] embedding : embeddings) {
                JSONObject metadata = new JSONObject()
                    .put("blockId", blockId)
                    .put("timestamp", System.currentTimeMillis());
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new JSONObject()
                        .put("embedding", embedding)
                        .put("metadata", metadata)
                        .toString()))
                    .build();

                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extendBlockWithContext(String block, MethodDeclaration method) {
        StringBuilder extendedBlock = new StringBuilder();

        // 添加方法的签名作为上下文
        extendedBlock.append(method.getDeclarationAsString()).append("\n");

        // 添加前置上下文，如上一行代码或相关注释
        method.getComment().ifPresent(comment -> extendedBlock.append(comment).append("\n"));
        // 添加前置上下文，如上一行代码或相关注释

        // 添加代码块本身
        extendedBlock.append(block);

        return extendedBlock.toString();
    }

    private String extendBlockWithClassContext(String block, ClassOrInterfaceDeclaration classDecl) {
        StringBuilder extendedBlock = new StringBuilder();

        // 添加类名、继承信息、实现的接口等
        extendedBlock.append(classDecl.getNameAsString());
        classDecl.getExtendedTypes().forEach(extendedType ->
                extendedBlock.append(" extends ").append(extendedType));
        classDecl.getImplementedTypes().forEach(implementedType ->
                extendedBlock.append(" implements ").append(implementedType));
        extendedBlock.append("\n");

        // 添加全局变量或静态方法
        classDecl.getFields().forEach(field ->
                extendedBlock.append(field.toString()).append("\n"));
        classDecl.getMethods().stream().filter(MethodDeclaration::isStatic)
                .forEach(method -> extendedBlock.append(method.toString()).append("\n"));

        // 添加代码块本身
        extendedBlock.append(block);

        return extendedBlock.toString();
    }

    /**
     * 对代码块进行分割时，可以追踪其依赖的函数调用链。对于某些复杂的函数，如果它依赖于多个子函数，可以将这些子函数的代码块或返回值纳入上下文。这有助于语义搜索理解代码块的实际功能
     * @param block
     * @param method
     * @param cu
     * @return
     */
    private String extendBlockWithFunctionDependencies(String block, MethodDeclaration method, CompilationUnit cu) {
        StringBuilder extendedBlock = new StringBuilder(block);

        // 分析方法调用，查找相关依赖函数
        method.findAll(MethodCallExpr.class).forEach(call -> {
            String calledMethodName = call.getNameAsString();
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(md -> md.getNameAsString().equals(calledMethodName))
                    .forEach(dependency -> extendedBlock.append("\n").append(dependency.toString()));
        });

        return extendedBlock.toString();
    }

    /**
     * 类继承和接口实现分析
     * 对于类或接口中定义的代码块，考虑将父类方法、接口方法的相关实现添加到上下文中。这样在语义搜索时可以考虑代码的继承关系，有助于识别重写方法或多态调用的语义。
     */
    private String extendBlockWithInheritanceContext(String block, ClassOrInterfaceDeclaration classDecl, CompilationUnit cu) {
        StringBuilder extendedBlock = new StringBuilder(block);

        // 处理父类的相关方法
        classDecl.getExtendedTypes().forEach(extendedType -> {
            cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(c -> c.getNameAsString().equals(extendedType.getNameAsString()))
                    .forEach(parentClass -> {
                        parentClass.getMethods().forEach(method ->
                                extendedBlock.append("\n").append(method.toString()));
                    });
        });

        // 处理实现的接口的相关方法
        classDecl.getImplementedTypes().forEach(implementedType -> {
            cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(c -> c.getNameAsString().equals(implementedType.getNameAsString()))
                    .forEach(interfaceDecl -> {
                        interfaceDecl.getMethods().forEach(method ->
                                extendedBlock.append("\n").append(method.toString()));
                    });
        });

        return extendedBlock.toString();
    }

}
