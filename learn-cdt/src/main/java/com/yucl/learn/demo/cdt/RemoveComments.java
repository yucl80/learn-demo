package com.yucl.learn.demo.cdt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveComments {
    public static void main(String[] args) throws IOException {

        String str = "D:\\jzjy\\c\\tbcreate.sqc";
        str = "D:\\jzjy\\sourcecode\\comnfunc\\bankbus.sqc";
        String sc = new String(Files.readAllBytes(Paths.get(str)),"GB2312");
        String  reg = "(?:\\/\\/(?:\\\\\\n|[^\\n])*\\n)|(?:\\/\\*[\\s\\S]*?\\*\\/)|((?:R\"([^(\\\\\\s]{0,16})\\([^)]*\\)\\2\")|(?:@\"[^\"]*?\")|(?:\"(?:\\?\\?'|\\\\\\\\|\\\\\"|\\\\\\n|[^\"])*?\")|(?:'(?:\\\\\\\\|\\\\'|\\\\\\n|[^'])*?'))";

        reg="/(?:\\/\\/(?:\\\\\\n|[^\\n])*\\n)|(?:\\/\\*[\\s\\S]*?\\*\\/)|((?:R\"([^(\\\\\\s]{0,16})\\([^)]*\\)\\2\")|(?:@\"[^\"]*?\")|(?:\"(?:\\?\\?'|\\\\\\\\|\\\\\"|\\\\\\n|[^\"])*?\")|(?:'(?:\\\\\\\\|\\\\'|\\\\\\n|[^'])*?'))/g";
        Pattern pattern = Pattern.compile(reg);

        Matcher matcher = pattern.matcher(sc);
        while (matcher.find()){
            System.out.println(matcher.group());
        }

        System.out.println("*******/***/*********");

        System.out.println(sc.replaceAll(reg," "));

    }
}
