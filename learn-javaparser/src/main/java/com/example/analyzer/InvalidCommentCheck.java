package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.LineComment;

import java.io.File;
import java.io.IOException;

public class InvalidCommentCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.getComments().forEach(comment -> {
                if (comment instanceof LineComment) {
                    String commentContent = comment.getContent().trim();
                    if (commentContent.isEmpty() || commentContent.equals("TODO") || commentContent.equals("FIXME")) {
                        result.addIssue("Invalid or placeholder comment found at line " + comment.getBegin().get().line + ": " + commentContent);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
