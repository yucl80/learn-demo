package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ServicA {
    @Autowired
    ServiceB serviceB;

    @Autowired
    CityMapper cityMapper;

    @Autowired
    CommentRepository commentRepository;
    public int cacl(int a, int b) {
        Thread t = new Thread(() -> serviceB.add(a, b));
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = cityMapper.findByState("test");

        commentRepository.countTotalCommentsByYear();

        Arrays.asList(1,2,3,4).stream().map(m -> serviceB.add(m,m)).map(s->s.toString()).map(Utils::hello).forEach(v->{
            System.out.println(v);
        });

        return str.length();
    }
}
