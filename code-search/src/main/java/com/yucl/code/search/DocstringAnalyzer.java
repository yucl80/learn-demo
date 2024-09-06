package com.yucl.code.search;

import com.github.javaparser.ast.body.MethodDeclaration;


public class DocstringAnalyzer {

    public String extractDocstrings(MethodDeclaration method) {
        StringBuilder docstrings = new StringBuilder();
        method.getComment().ifPresent(comment -> docstrings.append(comment.getContent()));
        
        
        return docstrings.toString();
    }
}
