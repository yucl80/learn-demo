package com.example.demo;



import java.util.ArrayList;
import java.util.List;

/**
 * NestClass
 */
public class NeClass {

    public void Hi() {
        System.out.println("hi");
    }

    static class A {
        public void test() {
            System.out.println("A.test");
        }
    }

    class B {
        public void f(){
            System.out.println("f()");
            class  X {
                public void funcX(){
                    System.out.println("funcX");
                }
            }
        }

        class C extends A {
            public void d(){
                System.out.println("d()");
            }
        }
    }

    //testfunc
    static List<String> testfunc(String testStr){
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
            }
        };
        return new ArrayList<>();
    }

}

class N{
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
