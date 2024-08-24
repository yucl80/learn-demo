package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateCodeCheck implements Check {
    private static final Set<String> METHOD_BODIES = new HashSet<>();
    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> blockSignatures = new HashSet<>();
//            cu.findAll(BlockStmt.class).forEach(block -> {
//                String blockSignature = block.toString();
//                if (blockSignatures.contains(blockSignature)) {
//                    result.addIssue("Duplicate code block found: " + blockSignature);
//                } else {
//                    blockSignatures.add(blockSignature);
//                }
//            });


            cu.findAll(MethodDeclaration.class).forEach(method -> {
                String body = method.getBody().map(b -> b.toString()).orElse("");
                if (METHOD_BODIES.contains(body)) {
                    result.addIssue("Duplicate code detected in method: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                } else {
                    METHOD_BODIES.add(body);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performCheck2(String codePath, CheckResult result) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));
            List<BlockStmt> blocks = cu.findAll(BlockStmt.class);
            List<Pair<BlockStmt, BlockStmt>> duplicates = new ArrayList<>();

            for (int i = 0; i < blocks.size(); i++) {
                for (int j = i + 1; j < blocks.size(); j++) {
                    if (blocks.get(i).equals(blocks.get(j))) {
                        duplicates.add(new Pair<>(blocks.get(i), blocks.get(j)));
                    }
                }
            }

            for (Pair<BlockStmt, BlockStmt> pair : duplicates) {
                result.addIssue("Duplicate code block found between lines " + pair.a.getBegin().get().line + " and " + pair.b.getBegin().get().line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
