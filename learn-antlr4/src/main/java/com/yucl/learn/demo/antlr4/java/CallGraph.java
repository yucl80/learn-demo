package com.yucl.learn.demo.antlr4.java; /***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
 ***/

import antlr4.java.Java8Lexer;
import antlr4.java.Java8Parser;
import antlr4.java.Java8ParserBaseListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

public class CallGraph {
    /**
     * A graph model of the output. Tracks call from one function to
     * another by mapping function to list of called functions. E.g.,
     * f -> [g, h]
     * Can dump DOT two ways (StringBuilder and ST). Sample output:
     * digraph G {
     * ... setup ...
     * f -> g;
     * g -> f;
     * g -> h;
     * h -> h;
     * }
     */
    static class Graph {
        // I'm using org.antlr.v4.runtime.misc: OrderedHashSet, MultiMap
        Set<String> nodes = new OrderedHashSet<String>(); // list of functions
        MultiMap<String, String> edges =                  // caller->callee
                new MultiMap<String, String>();

        public void edge(String source, String target) {
            edges.map(source, target);
        }

        public String toString() {
            return "edges: " + edges.toString() + ", functions: " + nodes;
        }

        public String toDOT() {
            StringBuilder buf = new StringBuilder();
            buf.append("digraph G {\n");
            buf.append("  ranksep=.25;\n");
            buf.append("  edge [arrowsize=.5]\n");
            buf.append("  node [shape=circle, fontname=\"ArialNarrow\",\n");
            buf.append("        fontsize=12, fixedsize=true, height=.45];\n");
            buf.append("  ");
            for (String node : nodes) { // print all nodes first
                buf.append(node);
                buf.append("; ");
            }
            buf.append("\n");
            for (String src : edges.keySet()) {
                for (String trg : edges.get(src)) {
                    buf.append("  ");
                    buf.append(src);
                    buf.append(" -> ");
                    buf.append(trg);
                    buf.append(";\n");
                }
            }
            buf.append("}\n");
            return buf.toString();
        }

        /**
         * Fill StringTemplate:
         * digraph G {
         * rankdir=LR;
         * <edgePairs:{edge| <edge.a> -> <edge.b>;}; separator="\n">
         * <childless:{f | <f>;}; separator="\n">
         * }
         * <p>
         * Just as an example. Much cleaner than buf.append method
         */
        public ST toST() {
            ST st = new ST(
                    "digraph G {\n" +
                            "  ranksep=.25; \n" +
                            "  edge [arrowsize=.5]\n" +
                            "  node [shape=circle, fontname=\"ArialNarrow\",\n" +
                            "        fontsize=12, fixedsize=true, height=.45];\n" +
                            "  <funcs:{f | <f>; }>\n" +
                            "  <edgePairs:{edge| <edge.a> -> <edge.b>;}; separator=\"\\n\">\n" +
                            "}\n"
            );
            st.add("edgePairs", edges.getPairs());
            st.add("funcs", nodes);
            return st;
        }
    }

    static class FunctionListener extends Java8ParserBaseListener {
        Graph graph = new Graph();
        String currentFunctionName = null;

        @Override
        public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
            currentFunctionName = ctx.getText();
            graph.nodes.add(currentFunctionName);
        }

        @Override
        public void exitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
            String funcName = ctx.getText();
            // map current function to the callee
            graph.edge(currentFunctionName, funcName);
        }


    }

    public static void main(String[] args) throws Exception {
        String inputFile = null;
        args = new String[]{"D:\\workspaces\\IdeaProjects\\java-call-graph\\call_analyser\\src\\main\\java\\org\\example\\App.java"};
        if (args.length > 0) inputFile = args[0];
        InputStream is = System.in;
        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }
        // CharStream input = new UnbufferedCharStream(is);
       // ANTLRInputStream input = new ANTLRInputStream(is);
       // Java8Lexer lexer = new Java8Lexer(input);
        Lexer lexer = new Java8Lexer(CharStreams.fromFileName(inputFile));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        parser.setBuildParseTree(true);


        ParseTree tree = parser.compilationUnit();
        // show tree in text form
//        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        FunctionListener collector = new FunctionListener();
        walker.walk(collector, tree);
        System.out.println(collector.graph.toString());
        System.out.println(collector.graph.toDOT());


        // Here's another example that uses StringTemplate to generate output
//        System.out.println(collector.graph.toST().render());
    }
}
