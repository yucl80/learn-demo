package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BizServiceImpl {
    private String name;

    private List<BizDto> bizDtoList;

    public  BizServiceImpl(String name){
        this.name = name;
    }

    public  BizServiceImpl(List<String> name){

    }

    int getCount(List<BizDto> bizS, int data){
        return 0;
    }

    static List<String>  testfunc(){
        return new ArrayList<>();
    }

    static void  testfunc(String testStr){

    }

    HashMap<String,BizDto> hello(String name, int a){
        return new HashMap<>();
    }
}
