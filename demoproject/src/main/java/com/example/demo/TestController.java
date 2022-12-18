package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("testController")
public class TestController {
    @Autowired
    ServicA servicA;

    @GetMapping("/hello")
    public String hello() {
        return "hello" + servicA.cacl(1, 2);
    }
}
