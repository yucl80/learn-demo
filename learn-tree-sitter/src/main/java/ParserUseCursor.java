import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class ParserUseCursor {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);
        TSNode rootNode = tree.getRootNode();
        TSTreeCursor cursor = new TSTreeCursor(rootNode);
        Map<String, List<MethodEntity>> classInfoMap = new HashMap<>();
        walk(cursor, sourceBytes, classInfoMap, "");
        System.out.println(classInfoMap);
    }

    private static void walk(TSTreeCursor cursor, byte[] sourceBytes, Map<String, List<MethodEntity>> classInfoMap, String outerClass) {
        if (cursor.gotoFirstChild()) {
            while (cursor.gotoNextSibling()) {
                TSNode currentNode = cursor.currentNode();
                if ("class_declaration".equals(currentNode.getType())) {
                    String className = getNodeText(currentNode.getChildByFieldName("name"), sourceBytes);
                    if (!outerClass.isEmpty()) {
                        className = outerClass + "." + className;
                    }
                    classInfoMap.put(className, new ArrayList<>());
                    TSNode classBody = currentNode.getChildByFieldName("body");
                    if (!classBody.isNull()) {
                        walk(new TSTreeCursor(classBody), sourceBytes, classInfoMap, className);
                    }
                } else if ("method_declaration".equals(currentNode.getType())) {
                    String methodName = getNodeText(currentNode.getChildByFieldName("name"), sourceBytes);
                    String methodBody =  getNodeText(currentNode.getChildByFieldName("body"), sourceBytes);
                    String methodSignature= buildMethodSignature(currentNode, sourceBytes);
                    MethodEntity methodEntity =new  MethodEntity(methodName,methodSignature,methodBody, currentNode.getStartByte(),currentNode.getEndByte());
                    classInfoMap.get(outerClass).add(methodEntity);
                }

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