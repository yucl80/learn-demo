package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

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
        return str.length();
    }
}
