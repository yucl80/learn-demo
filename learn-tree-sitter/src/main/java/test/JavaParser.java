package test;

import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class JavaParser {

    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);

        // Traverse the CST
        TSTreeCursor cursor = new TSTreeCursor(tree.getRootNode());
        if (cursor.gotoFirstChild()) {
            while (cursor.gotoNextSibling()) {
                TSNode currentNode = cursor.currentNode();
                System.out.println(currentNode.getType());
                if (currentNode.getType().equals("class_declaration")) {
                    printClassName(currentNode, sourceBytes);
//                    printMethods(currentNode, sourceBytes);
                }
            }
        }
    }

    private static void printClassName(TSNode node, byte[] sourceBytes) {
        // Find the identifier node within the class declaration
        TSTreeCursor cursor = new TSTreeCursor(node);
        if(cursor.gotoFirstChild()) {
            System.out.println(cursor.currentNode().getType());
            TSTreeCursor identifierCursor = new TSTreeCursor(cursor.currentNode());
            while (identifierCursor.gotoNextSibling()) {
                System.out.println(identifierCursor.currentNode().getType());
                if (identifierCursor.currentNode().getType().equals("identifier")) {
                    System.out.println("Class Name: " + getNodeText(identifierCursor.currentNode(), sourceBytes));
                    break;
                }
            }
        }
    }

    private static void printClassName1(TSTreeCursor cursor, byte[] sourceBytes) {
        // Find the identifier node within the class declaration
        if(cursor.gotoFirstChild()) {
            TSTreeCursor identifierCursor = new TSTreeCursor(cursor.currentNode());
            while (identifierCursor.gotoNextSibling()) {
                System.out.println(identifierCursor.currentNode().getType());
                if (identifierCursor.currentNode().getType().equals("identifier")) {
                    System.out.println("Class Name: " + getNodeText(identifierCursor.currentNode(), sourceBytes));
                    break;
                }
            }
        }
    }

    private static void printMethods(TSNode node, byte[] sourceBytes) {
        // Find the method declarations within the class
        TSTreeCursor cursor = new TSTreeCursor(node);
        if (cursor.gotoFirstChild()) {
            TSTreeCursor methodCursor = new TSTreeCursor( cursor.currentNode());
            while (methodCursor.gotoNextSibling()) {
                if (methodCursor.currentNode().getType().equals("method_declaration")) {
                    printMethodName(methodCursor,sourceBytes);
                    printMethodBody(methodCursor,sourceBytes);
                }
            }
        }
    }

    private static void printMethods(TSTreeCursor cursor, byte[] sourceBytes) {
        // Find the method declarations within the class
       if (cursor.gotoFirstChild()) {
           TSTreeCursor methodCursor = new TSTreeCursor( cursor.currentNode());
           while (methodCursor.gotoNextSibling()) {
               if (methodCursor.currentNode().getType().equals("method_declaration")) {
                   printMethodName(methodCursor,sourceBytes);
                   printMethodBody(methodCursor,sourceBytes);
               }
           }
       }
    }

    private static void printMethodName(TSTreeCursor cursor, byte[] sourceBytes) {
        // Find the identifier node within the method declaration
        if(cursor.gotoFirstChild()) {
            TSTreeCursor identifierCursor = new TSTreeCursor(cursor.currentNode());
            while (identifierCursor.gotoNextSibling()) {
                if (identifierCursor.currentNode().getType().equals("identifier")) {
                    System.out.println("Method Name: " + getNodeText(identifierCursor.currentNode(),sourceBytes));
                    break;
                }
            }
        }
    }

    private static void printMethodBody(TSTreeCursor cursor, byte[] sourceBytes) {
        // Find the block node within the method declaration
        if(cursor.gotoFirstChild()) {
            TSTreeCursor blockCursor = new TSTreeCursor( cursor.currentNode());
            while (blockCursor.gotoNextSibling()) {
                if (blockCursor.currentNode().getType().equals("block")) {
                    System.out.println("Method Body: " + getNodeText(blockCursor.currentNode(),sourceBytes));
                    break;
                }
            }
        }
    }

    private static String getNodeText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }
}