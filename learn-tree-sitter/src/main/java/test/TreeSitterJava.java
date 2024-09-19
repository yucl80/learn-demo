package test;

import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class TreeSitterJava {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);
        TSNode rootNode = tree.getRootNode();
        // 查找类声明节点
        extractClassAndMethodInfo(rootNode, sourceBytes);
    }



    private static void extractClassAndMethodInfo(TSNode node, byte[] sourceBytes) {
        // 检查节点类型
        if (node.getType().equals("class_declaration")) {
            String className = getNodeText(node.getChildByFieldName("name"), sourceBytes);
            System.out.println("Class Name: " + className);
            int childCount = node.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TSNode child = node.getChild(i);
                if (child.getType().equals("method_declaration")) {
                    extractMethodInfo(child, sourceBytes);
                }
            }
            System.out.println();
        } else if (node.getType().equals("method_declaration")) {
            extractMethodInfo(node, sourceBytes);
        }


        // 递归遍历子节点，处理嵌套类和子类
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TSNode child = node.getChild(i);

                extractClassAndMethodInfo(child, sourceBytes);

        }
    }

    private static void extractMethodInfo(TSNode methodNode, byte[] sourceBytes) {
        // 获取方法注释（如果存在）
        String methodComment = getComment(methodNode, sourceBytes);

        TSNode name = methodNode.getChildByFieldName("name");

        String methodName = getNodeText(name, sourceBytes);

        System.out.println("  Method Name: " + methodName);
        if (!methodComment.isEmpty()) {
            System.out.println("  Comment: " + methodComment);
        }
        String methodBody = getNodeText(methodNode, sourceBytes);

        System.out.println("  Method Body: \n" + methodBody);
    }


    private static void findMethodDeclarations(TSNode node, byte[] sourceBytes) {
        if (node.getType().equals("method_declaration")) {
            TSNode methodNameNode = findNodeByType(node, "identifier");
            if (methodNameNode != null) {
                System.out.println(getNodeText(methodNameNode, sourceBytes));

            }
            System.out.println(getComment(node, sourceBytes));
            System.out.println(getNodeText(node, sourceBytes));
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            findMethodDeclarations(child, sourceBytes);
        }
    }

    private static String getComment(TSNode node, byte[] sourceBytes) {
        TSNode commentNode = node.getPrevSibling(); // 注释通常在方法声明之前
        return commentNode != null && commentNode.getType().equals("block_comment") ? getNodeText(commentNode, sourceBytes) : "";
    }

    private static String getNodeText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }

    private static TSNode findNodeByType(TSNode node, String type) {
        if (node.getType().equals(type)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            TSNode child = node.getChild(i);
            TSNode result = findNodeByType(child, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

//    private static void extractCallRelations(TreeCursor cursor, String currentMethod, String sourceCode) {
//        // 移动到方法体的第一个子节点以查找调用
//        if (cursor.gotoFirstChild()) {
//            do {
//                if (cursor.currentNode().type().equals("call_expression")) { // 查找函数调用表达式
//                    String calledMethod = getCalledMethod(cursor, sourceCode);
//                    if (calledMethod != null) {
//                        callGraph.put(currentMethod, calledMethod); // 记录调用关系
//                    }
//                }
//            } while (cursor.gotoNextSibling());
//            cursor.gotoParent(); // 返回到方法节点
//        }
//    }
}