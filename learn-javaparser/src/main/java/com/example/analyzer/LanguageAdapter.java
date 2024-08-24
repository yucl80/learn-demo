package com.example.analyzer;

import java.util.List;

public interface LanguageAdapter {
    void parse(String codePath);
    List<Check> getChecks();
}
