package com.yucl.code.search;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

public class PatternBasedBlockSplitter {

    // 识别复杂的代码结构
    public List<String> splitMethodIntoComplexBlocks(MethodDeclaration method) {
        List<String> blocks = new ArrayList<>();
        method.getBody().ifPresent(body -> {
            StringBuilder block = new StringBuilder();
            for (Statement statement : body.getStatements()) {
                block.append(statement.toString());
                if (statement instanceof BlockStmt ||
                        statement instanceof IfStmt ||
                        statement instanceof ForStmt ||
                        statement instanceof WhileStmt ||
                        statement instanceof DoStmt ||
                        statement instanceof SwitchStmt ||
                        containsNestedLoopsOrRecursion(statement)) {

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

    private boolean containsNestedLoopsOrRecursion(Statement statement) {
        // 检查代码块中是否包含嵌套循环或递归调用
        // 使用更复杂的逻辑或模式匹配来检测这些结构
        return statement.toString().matches(".*(for|while|do).*\\{.*(for|while|do).*\\}") ||
                statement.toString().matches(".*\\b" + statement.getParentNode().get().toString() + "\\b.*");
    }
}
