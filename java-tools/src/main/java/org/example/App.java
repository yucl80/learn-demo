package org.example;

import java.util.HashSet;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String oldFileNameWithPath="D:\\workspaces\\IdeaProjects\\java-tools\\src\\main\\java\\org\\example\\A.java";
        String newFileNameWithPath="D:\\A.java";
        HashSet<String> changedMethods = MethodDiff.methodDiffInClass(
                oldFileNameWithPath,
                newFileNameWithPath
        );
        System.out.println(changedMethods);
    }
}
