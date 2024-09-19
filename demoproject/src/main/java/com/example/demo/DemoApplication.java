package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

   private ServiceB serviceB2;

   @Autowired
   private List<ServiceB> serviceBList;

    @Autowired
   public  void setData( @Qualifier("serviceBImpl2") ServiceB serviceB){
      this.serviceB2 = serviceB;
   }

   @Autowired
   private Map<String,ServiceB> stringServiceBMap;

   public DemoApplication(@Qualifier("serviceBImpl2") ServiceB serviceB){

       System.out.println(serviceB.getName());
   }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.serviceB2.getName());
        serviceBList.forEach(serviceB1 -> {
            System.out.println(serviceB1.toString());
        });

        stringServiceBMap.forEach(new BiConsumer<String, ServiceB>() {
            @Override
            public void accept(String s, ServiceB serviceB) {
                System.out.println(s );
            }
        });

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        map.forEach((info, method) -> {
            System.out.println(info + " " + method);
        });
    }

}


