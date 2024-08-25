package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BizServiceImpl {
   private String name;
   private List bizDtoList;

   public BizServiceImpl(String name) {
      this.name = name;
   }

   public BizServiceImpl(List name) {
   }

   int getCount(List bizS, int data) {
      return 0;
   }

   static List testfunc() {
      return new ArrayList();
   }

   static void testfunc(String testStr) {
   }

   HashMap hello(String name, int a) {
      return new HashMap();
   }
}
