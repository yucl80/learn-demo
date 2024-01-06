package com.yucl.learn.demo.bcel;

public class AnnoInfo {
    private boolean isController;
    private String basePath="";

    public boolean isController() {
        return isController;
    }

    public void setController(boolean controller) {
        isController = controller;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
