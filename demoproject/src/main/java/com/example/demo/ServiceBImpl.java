package com.example.demo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ServiceBImpl extends AbstractServiceB {
    public int add(int a, int b) {
        return a + b + 10;
    }
}
