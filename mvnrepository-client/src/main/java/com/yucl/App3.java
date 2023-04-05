package com.yucl;

public class App3 {
    public static void main(String[] args) {
        org.owasp.dependencycheck.App app = new org.owasp.dependencycheck.App();
        app.run(new String[]{"-s", "L:\\workspaces\\IdeaProjects\\learn-demo", "-f", "HTML","-d","L:\\workspaces\\IdeaProjects\\dependency-check-8.2.1-release\\dependency-check\\data"});
    }
}
