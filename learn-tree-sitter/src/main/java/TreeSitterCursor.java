import org.treesitter.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class TreeSitterCursor {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\BizServiceImpl.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);
        TSNode rootNode = tree.getRootNode();
        TSTreeCursor cursor = new TSTreeCursor(rootNode);
        String type = cursor.currentNode().getType();

        if (cursor.gotoFirstChild()) {
            walk(cursor, sourceBytes);
        }

    }

    private static void walk(TSTreeCursor cursor, byte[] sourceBytes) {
        if (cursor.gotoFirstChild()) {
            walk(cursor, sourceBytes);
            cursor.gotoParent();
        }
        while (cursor.gotoNextSibling()) {
            if ( cursor.currentFieldName()!=null) {
                System.out.println(cursor.currentFieldName() + " , " + cursor.currentNode().getType());

                System.out.println(getNodeText(cursor.currentNode(), sourceBytes));
            }

//            TSNode currentNode = cursor.currentNode();
//            if (currentNode.getType().equals("class_declaration")){
//                for(int i=0;i< currentNode.getChildCount();i++){
//                    TSNode node = currentNode.getChild(i);
//                    System.out.println(node.getType() + "   " + getNodeText(node,sourceBytes));
//                }
//                String className = getNodeText(currentNode, sourceBytes);
//                System.out.println("Class Name: " + className);
//            }else if(currentNode.getType().equals("method_declaration")){
//                String className = getNodeText(currentNode.getChild(0), sourceBytes);
//                System.out.println("Method Name: " + className);
//            }
            walk(cursor, sourceBytes);
        }
    }

    private static String getNodeText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }


}