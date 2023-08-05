package com.yucl;

/**
 * Hello world!
 *
 */
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPictureData;

import java.io.FileInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        parseVisioFile("L:\\workspaces\\IdeaProjects\\learn-demo\\visio-parse\\file\\a1.vsdx");
    }

    // read visio file ,get shape info, and shape relation info
    public static void parseVisioFile1(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            POIFSFileSystem fileSystem = new POIFSFileSystem(fileInputStream);
            try (HDGFDiagram hdgfDiagram = new HDGFDiagram(fileSystem)) {
                Stream[] streams = hdgfDiagram.getTopLevelStreams();
                for (Stream stream : streams) {
                    System.out.println(stream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     public static void parseVisioFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFDrawing drawing = sheet.getDrawingPatriarch();
                if (drawing != null) {
                    for (XSSFShape shape : drawing.getShapes()) {
                        // 获取图形的元数据
                        // ...
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}