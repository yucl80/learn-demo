package com.example.analyzer;

import java.util.ArrayList;
import java.util.List;

public class CheckResult {

    private List<String> issues;

    public CheckResult() {
        this.issues = new ArrayList<>();
    }

    public void addIssue(String issue) {
        issues.add(issue);
    }

    public void printIssues() {
        if (issues.isEmpty()) {
            System.out.println("No issues found.");
        } else {
            issues.forEach(System.out::println);
        }
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public List<String> getIssues() {
        return issues;
    }
}
