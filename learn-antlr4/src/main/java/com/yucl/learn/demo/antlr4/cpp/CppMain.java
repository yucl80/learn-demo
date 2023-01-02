package com.yucl.learn.demo.antlr4.cpp;

import antlr4.cpp.CPP14Lexer;
import antlr4.cpp.CPP14Parser;
import antlr4.cpp.CPP14ParserListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CppMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        File file = new File(args[0]);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ANTLRInputStream stream = new ANTLRInputStream(fis);
            CPP14Lexer lexer = new CPP14Lexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CPP14Parser parser = new CPP14Parser(tokens);

            parser.addErrorListener(new ConsoleErrorListener());
            parser.translationUnit();
            fis.close();
            int errorCode = 0;

            System.out.println("Finished. Result: " + errorCode + " file:///" + args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
