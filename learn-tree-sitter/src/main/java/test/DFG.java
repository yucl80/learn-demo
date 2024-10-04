package test;

import org.treesitter.TSNode;
import org.treesitter.TSPoint;

import java.util.*;

import static test.CodeCleaner.treeToVariableIndex;

public class DFG {
    public static List<DFGEntry> DFG_java(TSNode rootNode, Map<int[], String> indexToCode, Map<String, List<int[]>> states) {
        List<String> assignment = Arrays.asList("assignment_expression");
        List<String> defStatement = Arrays.asList("variable_declarator");
        List<String> incrementStatement = Arrays.asList("update_expression");
        List<String> ifStatement = Arrays.asList("if_statement", "else");
        List<String> forStatement = Arrays.asList("for_statement");
        List<String> enhancedForStatement = Arrays.asList("enhanced_for_statement");
        List<String> whileStatement = Arrays.asList("while_statement");


        List<DFGEntry> dfgEntries = new ArrayList<>();

        if ((rootNode.getChildCount()==0 || rootNode.getType().equals("string")) && !rootNode.getType().equals("comment")) {
            int[] index = {rootNode.getStartByte(), rootNode.getEndByte()};
            String code = indexToCode.get(index);
            if (rootNode.getType().equals(code)) {
                return Collections.emptyList();
            } else if (states.containsKey(code)) {
                dfgEntries.add(new DFGEntry(code, index, "comesFrom", new ArrayList<>()));
            } else {
                if (rootNode.getType().equals("identifier")) {
                    states.put(code, new ArrayList<>(Collections.singletonList(index)));
                }
                dfgEntries.add(new DFGEntry(code, index, "comesFrom", new ArrayList<>()));
            }
            return dfgEntries;
        } else if (defStatement.contains(rootNode.getType())) {
            TSNode name = rootNode.getChildByFieldName("name");
            TSNode value = rootNode.getChildByFieldName("value");
            if (value == null) {
                List<int[]>  indexes = treeToVariableIndex(name, indexToCode);
                for (int[] index : indexes) {
                    String code = indexToCode.get(index);
                    dfgEntries.add(new DFGEntry(code, index, "comesFrom", new ArrayList<>()));
                    states.put(code, new ArrayList<>(Collections.singletonList(index)));
                }
                return dfgEntries;
            } else {
                // Handle value assignment
                List<DFGEntry> valueEntries = DFG_java(value, indexToCode, states);
                dfgEntries.addAll(valueEntries);
                List<int[]> nameIndexes = treeToVariableIndex(name, indexToCode);
                for (int[] index : nameIndexes) {
                    String code = indexToCode.get(index);
                    dfgEntries.add(new DFGEntry(code, index, "assignedTo", new ArrayList<>()));
                    states.put(code, new ArrayList<>(Collections.singletonList(index)));
                }
                return dfgEntries;
            }
        } else if (assignment.contains(rootNode.getType())) {
            // Handle assignment expressions
            TSNode left = rootNode.getChildByFieldName("left");
            TSNode right = rootNode.getChildByFieldName("right");
            List<DFGEntry> rightEntries = DFG_java(right, indexToCode, states);
            dfgEntries.addAll(rightEntries);
            List<int[]> leftIndexes = treeToVariableIndex(left, indexToCode);
            for (int[] index : leftIndexes) {
                String code = indexToCode.get(index);
                dfgEntries.add(new DFGEntry(code, index, "assignedTo", new ArrayList<>()));
                states.put(code, new ArrayList<>(Collections.singletonList(index)));
            }
            return dfgEntries;
        } else if (ifStatement.contains(rootNode.getType())) {
            // Handle if statements
            TSNode condition = rootNode.getChildByFieldName("condition");
            List<DFGEntry> conditionEntries = DFG_java(condition, indexToCode, states);
            dfgEntries.addAll(conditionEntries);
            // Handle the body of the if statement
            TSNode body = rootNode.getChildByFieldName("body");
            if (body != null) {
                dfgEntries.addAll(DFG_java(body, indexToCode, states));
            }
            return dfgEntries;
        } else if (forStatement.contains(rootNode.getType())) {
            // Handle for statements
            TSNode init = rootNode.getChildByFieldName("init");
            TSNode condition = rootNode.getChildByFieldName("condition");
            TSNode update = rootNode.getChildByFieldName("update");
            TSNode body = rootNode.getChildByFieldName("body");
            if (init != null) {
                dfgEntries.addAll(DFG_java(init, indexToCode, states));
            }
            if (condition != null) {
                dfgEntries.addAll(DFG_java(condition, indexToCode, states));
            }
            if (update != null) {
                dfgEntries.addAll(DFG_java(update, indexToCode, states));
            }
            if (body != null) {
                dfgEntries.addAll(DFG_java(body, indexToCode, states));
            }
            return dfgEntries;
        } else if (whileStatement.contains(rootNode.getType())) {
            // Handle while statements
            TSNode condition = rootNode.getChildByFieldName("condition");
            TSNode body = rootNode.getChildByFieldName("body");
            if (condition != null) {
                dfgEntries.addAll(DFG_java(condition, indexToCode, states));
            }
            if (body != null) {
                dfgEntries.addAll(DFG_java(body, indexToCode, states));
            }
            return dfgEntries;
        } else if (incrementStatement.contains(rootNode.getType())) {
            // Handle increment statements
            TSNode argument = rootNode.getChildByFieldName("argument");
            List<DFGEntry> argumentEntries = DFG_java(argument, indexToCode, states);
            dfgEntries.addAll(argumentEntries);
            return dfgEntries;
        }
        // Handle other node types as needed
        return dfgEntries;
    }
}
