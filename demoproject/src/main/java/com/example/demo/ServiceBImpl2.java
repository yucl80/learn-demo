package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class ServiceBImpl2 extends AbstractServiceB {
    @Override
    public int add(int a, int b) {
        return super.add(a, b);
    }

    @Override
    public String getName() {
        return "ServiceBImpl_2";
    }

    public String hello() {
        return "ok";
    }
}
