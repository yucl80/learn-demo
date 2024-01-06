package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {
    @Autowired
    ServicA servicA;

    @GetMapping(path="/hello",name="hello")
    public String hello() {
        return "hello" + servicA.cacl(1, 2);
    }
}
