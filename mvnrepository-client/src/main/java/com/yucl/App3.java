package com.yucl;

public class App3 {
    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "51943");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "51943");
        org.owasp.dependencycheck.App app = new org.owasp.dependencycheck.App();
        app.run(new String[]{"-s", "L:\\workspaces\\IdeaProjects\\learn-demo", "--advancedHelp", "-f", "HTML", "-d", "L:\\workspaces\\IdeaProjects\\dependency-check-8.2.1-release\\dependency-check\\data"});
    }
}
