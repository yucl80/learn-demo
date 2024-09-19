package com.example.demo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class ServiceBImpl extends AbstractServiceB {


    @Override
    public String getName() {
        return "ServiceBImpl_1";
    }
}
