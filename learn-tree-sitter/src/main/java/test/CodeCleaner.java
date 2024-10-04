package test;

import org.treesitter.TSNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeCleaner {

    public static String removeCommentsAndDocstrings(String source, String lang) throws IOException {
        if ("python".equals(lang)) {
            return removePythonCommentsAndDocstrings(source);
        } else if ("ruby".equals(lang)) {
            return source;
        } else {
            return removeOtherLanguageComments(source);
        }
    }

    private static String removePythonCommentsAndDocstrings(String source) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new StringReader(source));
        String line;
        boolean inDocstring = false;
        while ((line = reader.readLine()) != null) {
            line = line.strip(); // 去除前后空格
            if (line.startsWith("\"\"\"") || line.startsWith("'''")) {
                inDocstring = !inDocstring; // 切换状态
                continue; // 跳过 docstring
            }
            if (inDocstring || line.startsWith("#")) {
                continue; // 跳过注释或 docstring
            }
            out.append(line).append("\n");
        }
        return out.toString().strip();
    }

    private static String removeOtherLanguageComments(String source) {
        Pattern pattern = Pattern.compile("//.*?$|/\\*.*?\\*/|\'(?:\\\\.|[^\\\\\'])*\'|\"(?:\\\\.|[^\\\"])*\"", Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group();
            if (match.startsWith("//")) {
                matcher.appendReplacement(result, " "); // 用空格替换单行注释
            } else {
                matcher.appendReplacement(result, match); // 保留字符串
            }
        }
        matcher.appendTail(result);
        String[] lines = result.toString().split("\n");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            if (!line.strip().isEmpty()) {
                out.append(line).append("\n");
            }
        }
        return out.toString().strip();
    }

    public static List<int[]> treeToTokenIndex(TSNode rootNode) {
        List<int[]> codeTokens = new ArrayList<>();
        if ((rootNode.getChildCount()==0 || "string".equals(rootNode.getType())) && !"comment".equals(rootNode.getType())) {
            codeTokens.add(new int[]{rootNode.getStartByte(), rootNode.getEndByte()});
        } else {
            for (int i=0;i<rootNode.getChildCount();i++ ) {
                TSNode child = rootNode.getChild(i);
                codeTokens.addAll(treeToTokenIndex(child));
            }
        }
        return codeTokens;
    }

    public static List<int[]> treeToVariableIndex(TSNode rootNode, Map<int[], String> indexToCode) {
        List<int[]> codeTokens = new ArrayList<>();
        if ((rootNode.getChildCount()==0 || "string".equals(rootNode.getType())) && !"comment".equals(rootNode.getType())) {
            int[] index = {rootNode.getStartByte(), rootNode.getEndByte()};
            String code = indexToCode.get(index);
            if (!rootNode.getType().equals(code)) {
                codeTokens.add(index);
            }
        } else {
            for (int i=0;i<rootNode.getChildCount();i++ ) {
                TSNode child = rootNode.getChild(i);
                codeTokens.addAll(treeToVariableIndex(child, indexToCode));
            }
        }

        return codeTokens;
    }

    public static String indexToCodeToken(int[] index, String[] code) {
        int startPoint = index[0];
        int endPoint = index[1];
        StringBuilder sb = new StringBuilder();
        if (startPoint == endPoint) {
            sb.append(code[startPoint].substring(startPoint, endPoint));
        } else {
            sb.append(code[startPoint].substring(startPoint));
            for (int i = startPoint + 1; i < endPoint; i++) {
                sb.append(code[i]);
            }
            sb.append(code[endPoint].substring(0, endPoint));
        }
        return sb.toString();
    }


}

