import org.treesitter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TreeSitterQuery {
    public static void main(String[] args) throws IOException {
        String javaSource = Files.readString(Paths.get("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\NeClass.java"));
        TSParser parser = new TSParser();
        TSLanguage java = new org.treesitter.TreeSitterJava();
        parser.setLanguage(java);
        byte[] sourceBytes = javaSource.getBytes(StandardCharsets.UTF_8);
        TSTree tree = parser.parseStringEncoding(null, javaSource, TSInputEncoding.TSInputEncodingUTF8);

        TSQuery tsQuery = new TSQuery(java, "(class_declaration\n" +
                "  name: (identifier) @name.definition.class) @definition.class");

//        System.out.println(tsQuery.getCaptureCount());
//        System.out.println(tsQuery.getCaptureNameForId(0));
//        System.out.println(tsQuery.getCaptureQuantifierForId(0, 0));


        TSQueryCursor queryCursor = new TSQueryCursor();

        queryCursor.exec(tsQuery, tree.getRootNode());
        TSQueryCursor.TSMatchIterator matchIterator = queryCursor.getMatches();
        while (matchIterator.hasNext()) {
            TSQueryMatch match = matchIterator.next();
//            System.out.println("captures length :" + match.getCaptures().length);
//            System.out.println("getPatternIndex :" + match.getPatternIndex());
//            System.out.println("getCaptureIndex:" + match.getCaptureIndex());
//            System.out.println("match getId :" + match.getId());
//            Arrays.asList(match.getCaptures()).forEach(tsQueryCapture -> {
//                System.out.println(getText(tsQueryCapture.getNode(), sourceBytes));
//            });


            TSNode node = match.getCaptures()[0].getNode();
            List<String> sb = new ArrayList<>();
            while (!node.isNull()) {
                if (node.getType().equals("class_declaration")) {
                    sb.add(0, getText(node.getChildByFieldName("name"), sourceBytes));
                }
                node = node.getParent();
            }
            String className = String.join(".", sb);
            System.out.println("class name:" + className);

            Query(java, match.getCaptures()[0].getNode(),sourceBytes);

        }
    }

    private static void Query(TSLanguage java, TSNode node, byte[] sourceBytes) {
        TSQuery tsQuery = new TSQuery(java, "(method_declaration\n" +
                "  name: (identifier) @name.definition.method) @definition.method");
//
//        System.out.println(tsQuery.getCaptureCount());
//        System.out.println(tsQuery.getCaptureNameForId(0));
//        System.out.println(tsQuery.getCaptureQuantifierForId(0, 0));

        TSQueryCursor queryCursor = new TSQueryCursor();
        queryCursor.exec(tsQuery, node);
        TSQueryCursor.TSMatchIterator matchIterator = queryCursor.getMatches();
        while (matchIterator.hasNext()) {
            TSQueryMatch match = matchIterator.next();
//            System.out.println("captures length :" + match.getCaptures().length);
//            System.out.println("getPatternIndex :" + match.getPatternIndex());
//            System.out.println("getCaptureIndex:" + match.getCaptureIndex());
//            System.out.println("match getId :" + match.getId());
            Arrays.asList(match.getCaptures()).forEach(tsQueryCapture -> {
                System.out.println(getText(tsQueryCapture.getNode(), sourceBytes));
            });

        }
    }


    private static String getText(TSNode node, byte[] sourceBytes) {
        int startByte = node.getStartByte();
        int endByte = node.getEndByte();
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }

    private static String getText(TSQuery tsQuery, int patternIndex, byte[] sourceBytes) {
        int startByte = tsQuery.getStartByteForPattern(patternIndex);
        int endByte = tsQuery.getEndByteForPattern(patternIndex);
        byte[] nodeBytes = Arrays.copyOfRange(sourceBytes, startByte, endByte);
        return new String(nodeBytes, StandardCharsets.UTF_8);
    }


}