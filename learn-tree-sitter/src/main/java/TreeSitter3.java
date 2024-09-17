import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class TreeSitter3 {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);
        TSNode rootNode = tree.getRootNode();
        TSTreeCursor cursor = new TSTreeCursor(rootNode);
        String type = cursor.currentNode().getType();
//        walkImports(rootNode,sourceBytes);

        walkClass(rootNode,sourceBytes);
//        if (cursor.gotoFirstChild()) {
//            walk(cursor, sourceBytes);
//        }

    }

    private static void walkImports(TSNode rootNode,byte[] sourceBytes){
        int childCount = rootNode.getChildCount();
        for(int i=0;i<childCount;i++){
            TSNode node = rootNode.getChild(i);
            if(node.getType().equals("import_declaration")){
                System.out.println(getNodeText(node,sourceBytes));
            }
            System.out.println(node.getType());
        }
    }

    private static void walkClass(TSNode rootNode,byte[] sourceBytes){
        int childCount = rootNode.getChildCount();
        for(int i=0;i<childCount;i++){
            TSNode node = rootNode.getChild(i);
            if(node.getType().equals("class_declaration")){
                String className = getNodeText(node.getChildByFieldName("name"),sourceBytes);
                System.out.println("className:" + className);

                walkMethod(node,sourceBytes);
            }
            System.out.println(node.getType());
        }
    }

    private static void walkMethod(TSNode classNode,byte[] sourceBytes){
        int childCount = classNode.getChildCount();
        for(int i=0;i<childCount;i++){
            TSNode node = classNode.getChild(i);
            if(node.getType().equals("method_declaration")){
                String methodName = getNodeText(node.getChildByFieldName("name"),sourceBytes);

                System.out.println("methodName:" + methodName);
            }
            System.out.println(node.getType());
        }
    }


    private static void walk(TSTreeCursor cursor, byte[] sourceBytes) {
        do {
            System.out.println(cursor.currentNode().getType());
            if (cursor.currentNode().getType().equals("class_declaration")) {

                String className = extractClassInfo(cursor.currentNode(), sourceBytes);
                System.out.println("Class Name: " + className);

                // 移动到类的第一个子节点
                cursor.gotoFirstChild();
                do {
                    if (cursor.currentNode().getType().equals("method_declaration")) {
                        extractMethodInfo(cursor.currentNode(), sourceBytes);
                    }
                } while (cursor.gotoNextSibling());

                // 返回到父节点（类）
                cursor.gotoParent();
                System.out.println();
            }
        } while (cursor.gotoNextSibling());


    }

    private static String extractClassInfo(TSNode node,byte[] sourceBytes ){
        for(int i=0;i< node.getChildCount();i++){
            TSNode child = node.getChild(i);
            if(child.getType().equals("identifier")) {
                System.out.println(getNodeText(child, sourceBytes));
            }
        }
        return "";
    }

    private static String extractMethodInfo(TSNode node,byte[] sourceBytes ){
         for(int i=0;i< node.getChildCount();i++){
             TSNode child = node.getChild(i);
             if(child.getType().equals("identifier")) {
                 System.out.println(getNodeText(child, sourceBytes));
             }
         }
         return "";
    }

    private static String getNodeText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }


}