package test;

import org.treesitter.TreeSitterJava;
import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ParserUseQuery {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);
        TSQuery tsQuery = new TSQuery(java, "(class_declaration\n" +
                "  name: (identifier) @name.definition.class) @definition.class");
        TSQueryCursor queryCursor = new TSQueryCursor();
        queryCursor.exec(tsQuery, tree.getRootNode());
        TSQueryCursor.TSMatchIterator matchIterator = queryCursor.getMatches();
        while (matchIterator.hasNext()) {
            TSQueryMatch match = matchIterator.next();
            TSNode node = match.getCaptures()[0].getNode();
            List<String> sb = new ArrayList<>();
            boolean foundClassInMethod = false;
            while (!node.isNull()) {
                if ("class_declaration".equals(node.getType())) {
                    sb.add(0, getNodeText(node.getChildByFieldName("name"), sourceBytes));
                } else if ("method_declaration".equals(node.getType())) {
                    foundClassInMethod = true;
                    break;
                }
                node = node.getParent();
            }
            if (!foundClassInMethod) {
                String className = String.join(".", sb);
                System.out.println("class name:" + className);
                listMethods(java, match.getCaptures()[0].getNode(), sourceBytes);
            }
        }
    }

    private static void listMethods(TSLanguage java, TSNode node, byte[] sourceBytes) {
        TSQuery tsQuery = new TSQuery(java, "(method_declaration) @definition.method");
        TSQueryCursor queryCursor = new TSQueryCursor();
        queryCursor.exec(tsQuery, node);
        TSQueryCursor.TSMatchIterator matchIterator = queryCursor.getMatches();
        while (matchIterator.hasNext()) {
            TSQueryMatch match = matchIterator.next();
            TSNode methodDeclarationNode = match.getCaptures()[0].getNode();
            TSNode parent = methodDeclarationNode.getParent().getParent();
            if (TSNode.eq(node, parent)) {
                String methodBody = getNodeText(methodDeclarationNode, sourceBytes);
                System.out.println("--" + buildMethodSignature(methodDeclarationNode, sourceBytes));
                System.out.println(methodBody);
            }

        }
    }


    private static void listMethods2(TSLanguage java, TSNode node, byte[] sourceBytes) {
        TSQuery tsQuery = new TSQuery(java, "(method_declaration) @definition.method");
        TSQueryCursor queryCursor = new TSQueryCursor();
        queryCursor.exec(tsQuery, node);
        TSQueryCursor.TSMatchIterator matchIterator = queryCursor.getMatches();
        while (matchIterator.hasNext()) {
            TSQueryMatch match = matchIterator.next();
            TSNode methodDeclarationNode = match.getCaptures()[0].getNode();
            TSNode parent = methodDeclarationNode.getParent().getParent();
            if (TSNode.eq(node, parent)) {
                String methodBody = getNodeText(methodDeclarationNode, sourceBytes);
                System.out.println("--" + buildMethodSignature(methodDeclarationNode, sourceBytes));
                System.out.println(methodBody);
            }

        }
    }

    private static String buildMethodSignature(TSNode methodDeclaration, byte[] sourceBytes) {
        TSNode returnTypeNode = methodDeclaration.getChildByFieldName("type");
        String returnType = returnTypeNode.isNull() ? "void" : getNodeText(returnTypeNode, sourceBytes);

        TSNode methodNameNode = methodDeclaration.getChildByFieldName("name");
        String methodName = getNodeText(methodNameNode, sourceBytes);

        List<String> parameters = new ArrayList<>();
        TSNode parametersNode = methodDeclaration.getChildByFieldName("parameters");
        if (!parametersNode.isNull()) {
            TSTreeCursor cursor = new TSTreeCursor(parametersNode);
            if (cursor.gotoFirstChild()) {
                while (cursor.gotoNextSibling()) {
                    TSNode parameter = cursor.currentNode();
                    TSNode typeNode = parameter.getChildByFieldName("type");
                    if (!typeNode.isNull()) {
                        String type = getNodeText(typeNode, sourceBytes);
                        TSNode nameNode = parameter.getChildByFieldName("name");
                        String name = nameNode.isNull() ? "" : getNodeText(nameNode, sourceBytes);
                        parameters.add(type + " " + name);
                    }
                }
            }
        }

        return returnType + " " + methodName + "(" + String.join(",", parameters) + ")";
    }


    private static String getNodeText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }


}