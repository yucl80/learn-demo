package com.yucl.learn.demo.bcel;

import org.apache.bcel.generic.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class RegeExtractor {

    public static void main(String[] args) {
        String input = "(Ljava/util/List<Lcom/example/demo/BizDto;>;Ljava/util/Map<Ljava/lang/String;Lcom/example/demo/BizDto;>;Ljava/util/List<Ljava/util/List<Lcom/example/demo/BizDto;>;>;BIJLjava/util/Date;CDF)I";
        extracted(input);

    }

    private static void extracted(String text) {
        List<String> matches = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        StringBuilder currentMatch = new StringBuilder();
        for (int i = 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == 'L' && stack.isEmpty()) {
                stack.push(i);
            } else if (c == '<' && text.charAt(i + 1) == 'L') {
                stack.push(i);
            } else if (c == ';') {
                int begin = stack.pop();
                if (stack.isEmpty()) {
                    String exp = text.substring(begin+1, i);
                    exp = exp.replaceAll("<L","<");
                    exp = exp.replaceAll(";>",">");
                    exp =exp.replaceAll(";L",",");
                    exp = exp.replaceAll("/",".");
                    matches.add(exp);
                }
            }else {
                if(stack.isEmpty()){
                    Type[] t = Type.getArgumentTypes("(" + c + ")");
                    Arrays.asList(t).forEach(s-> matches.add(s.toString()));
                }else{
                    if(c=='L' && text.charAt(i-1) == ';'){
                        stack.push(i);
                    }
                }
            }
        }

        System.out.println(matches);
    }

    private static void extracted2(String input) {
        List<String> matches = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        StringBuilder currentMatch = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == 'L') {
                if (currentMatch.length() == 0) { // Start of a new match
                    currentMatch.append(c);
                }
            } else if (c == '<') {
                if (stack.isEmpty() && currentMatch.length() > 0) {
                    stack.push(i);
                } else {
                    currentMatch.append(c);
                }
            } else if (c == '>') {
                if (!stack.isEmpty()) {
                    currentMatch.append(c);
                    if (stack.size() == 1) { // End of the nested part
                        int startIndex = stack.pop();
                        currentMatch.append(';');
                        matches.add(currentMatch.toString());
                        currentMatch = new StringBuilder();
                    } else {
                        stack.pop();
                    }
                } else {
                    // This is not part of a nested type, reset the current match
                    currentMatch = new StringBuilder();
                }
            } else if (c == ';') {
                if (currentMatch.length() > 0 && stack.isEmpty()) {
                    currentMatch.append(c);
                    matches.add(currentMatch.toString());
                    currentMatch = new StringBuilder();
                }
            } else {
                if (currentMatch.length() > 0) {
                    currentMatch.append(c);
                }
            }
        }
        System.out.println(matches);
    }
}
