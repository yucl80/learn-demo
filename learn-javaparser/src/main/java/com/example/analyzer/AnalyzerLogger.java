package com.example.analyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnalyzerLogger {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void log(String message) {
        System.out.println(dtf.format(LocalDateTime.now()) + " - " + message);
    }
}
