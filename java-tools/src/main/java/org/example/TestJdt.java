package org.example;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

public class TestJdt {
    public static void main(String[] args) throws Exception {
        // create a project with name "TESTJDT"

        org.eclipse.core.resources.IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject("TESTJDT");
        project.create(null);
        project.open(null);

        Thread.sleep(10000);
//set the Java nature
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[]{JavaCore.NATURE_ID});

//create the project
        project.setDescription(description, null);
        IJavaProject javaProject = JavaCore.create(project);

//set the build path
        IClasspathEntry[] buildPath = {
                JavaCore.newSourceEntry(project.getFullPath().append("src")),
                JavaRuntime.getDefaultJREContainerEntry()};

        javaProject.setRawClasspath(buildPath, project.getFullPath().append(
                "bin"), null);

//create folder by using resources package
        IFolder folder = project.getFolder("src");
        folder.create(true, true, null);

//Add folder to Java element
        IPackageFragmentRoot srcFolder = javaProject
                .getPackageFragmentRoot(folder);

//create package fragment
        IPackageFragment fragment = srcFolder.createPackageFragment(
                "com.programcreek", true, null);

//init code string and create compilation unit
        String str = "package com.programcreek;" + "\n"
                + "public class Test  {" + "\n" + " private String name;"
                + "\n" + "}";

        ICompilationUnit cu = fragment.createCompilationUnit("Test.java", str,
                false, null);

//create a field
        IType type = cu.getType("Test");

        type.createField("private String age;", null, true, null);
    }

}
