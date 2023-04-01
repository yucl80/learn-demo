package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.internal.formatter.CCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormatCode {
    public static void main(String[] args) throws IOException, BadLocationException {
        CodeFormatter formatter = new CCodeFormatter();

        String sourcecode = new String(Files.readAllBytes(Paths.get("D:\\archguard\\learn-demo-main\\learn-cdt\\src\\test\\resources\\a.c")), StandardCharsets.UTF_8);
        System.out.println(sourcecode);
        TextEdit edit = formatter.format(CodeFormatter.K_TRANSLATION_UNIT, sourcecode, 0, sourcecode.length(), 0, null);

        IDocument doc = new Document(sourcecode);
        edit.apply(doc);
        System.out.println(doc.get());
    }
}
