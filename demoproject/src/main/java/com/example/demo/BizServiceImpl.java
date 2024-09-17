package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test class
 */
public class BizServiceImpl {
    private String name;

    private List<BizDto> bizDtoList;

    public  BizServiceImpl(String name){
        this.name = name;
    }

    public <T,KS>  List<String> getStr(List<T> alist, Map<String,KS> map){
        return null;
    }

    public  BizServiceImpl(List<String> name){

    }

    int getCount(List<BizDto> bizS, int data){
        return 0;
    }

    static List<String>  testfunc(){
        /**
         * create new array
         */
        return new ArrayList<>();
    }

    static void  testfunc(String testStr){

    }

    /**
     * 打招呼函数
     * @param name
     * @param a
     * @return
     */
    HashMap<String,BizDto> hello(String name, int a){
        //new map
        return new HashMap<>();
    }
}
