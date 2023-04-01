package com.yucl.learn.demo.cdt.parser.c;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SqcParser {

    public String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        StringBuilder sql = new StringBuilder();
        StringBuilder fileBody = new StringBuilder();
        boolean find = false;
        int n = 0;
        while ((line = reader.readLine()) != null) {
            String trimLine = line.trim();
            if (trimLine.startsWith("EXEC") && trimLine.length() > 4 && trimLine.substring(4).trim().startsWith("SQL")) {
                sql.append(line).append("\n");
                find = true;
                n = n + 1;
            } else if (find) {
                sql.append(line.trim()).append("\n");
            }
            if (!find) {
                fileBody.append(line).append("\n");
            }
            if (trimLine.endsWith(";") && find) {
                System.out.println(sql);
                find = false;
                fileBody.append("callSQL(").append(sql).append("\");\n");
                sql = new StringBuilder();
            }
        }
        return fileBody.toString();
    }
}
