package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    ServicA servicA;
    @GetMapping(path="/hello",name="hello")
    public String hello() {
        return "hello" + servicA.cacl(1, 2);
    }
}
