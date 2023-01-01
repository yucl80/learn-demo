package com.example.demo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class ServiceBImpl extends AbstractServiceB {

    private static Map<String,String> map = new HashMap<>();

    private static int xx = 110;

    private int a =100 ;

    private int b= 0 ;

    private Object obj ;

    private  Object test ;

    public ServiceBImpl(){
        b = 100;
        test = null;
        obj = new Object();
    }

    static {
        map.put("test","adfsdfsdas");
        map.put("xxxx","fsdfasdf");
    }

    public int add(int a, int b) {
        return a + b + 10;
    }

    public int f1(int x){
        String key = "test";
        String s= map.get(key);
        System.out.println(s);
        int bb = getB();
        int m = add(100, bb + 10);
        return a + x + m;
    }

    public int getB(){
        return b;
    }
}
