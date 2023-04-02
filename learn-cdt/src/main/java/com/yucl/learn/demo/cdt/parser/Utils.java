package com.yucl.learn.demo.cdt.parser;

import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.internal.formatter.CCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Utils {
    static final String regex = "(?:\\/\\/(?:\\\\\\n|[^\\n])*\\n)|(?:\\/\\*[\\s\\S]*?\\*\\/)|((?:R\"([^(\\\\\\s]{0,16})\\([^)]*\\)\\2\")|(?:@\"[^\"]*?\")|(?:\"(?:\\?\\?'|\\\\\\\\|\\\\\"|\\\\\\n|[^\"])*?\")|(?:'(?:\\\\\\\\|\\\\'|\\\\\\n|[^'])*?'))";
    static final Pattern pattern = Pattern.compile(regex);

    public static String formatCode(String sourceCode) {
        if (sourceCode != null && !sourceCode.isEmpty()) {
            String sourceText = sourceCode.replaceAll("\n[\t|\\s]*[\n]*", "\n");
            try {
                CodeFormatter formatter = new CCodeFormatter();
                TextEdit edit = formatter.format(CodeFormatter.K_UNKNOWN, sourceText, 0, sourceText.length(), 0, null);
                IDocument doc = new Document(sourceText);
                edit.apply(doc);
                return doc.get();
            } catch (Exception e) {
                return sourceText;
            }
        } else {
            return sourceCode;
        }
    }

    public static String removeComments(String sourceCode) {
        return sourceCode.replaceAll(regex, " ");
    }

    public static String getFunctionBodyHashCode(String sourceCode) {
        String codeText = removeComments(sourceCode);
        codeText = formatCode(codeText);
        return getHashCode(codeText);
    }

    static String getHashCode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
