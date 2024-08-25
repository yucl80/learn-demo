package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void exec(String str) {
        String sql = "select * from user where id = " + str;
        System.out.println(sql);
        jdbcTemplate.queryForObject(sql, Object.class);

    }
}
