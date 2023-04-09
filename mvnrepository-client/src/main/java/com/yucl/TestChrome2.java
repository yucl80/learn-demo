package com.yucl;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.util.List;

public class TestChrome2 {
    public static void main(String[] args) throws InterruptedException, IOException {
       // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

      //  WebDriverManager.chromedriver().setup();

       // WebDriver driver = new ChromeDriver();

      /*  ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("disable-gpu");
*/


        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--no-sandbox");
       // edgeOptions.addArguments("--headless");
        edgeOptions.addArguments("disable-gpu");

        WebDriver driver = new EdgeDriver(edgeOptions);

        driver.get("https://www.cnvd.org.cn/flaw/typelist?typeId=28");

        //blob:https://www.cnnvd.org.cn/8870bdad-f8ea-4d4b-b30d-d4f7fd0cfff8

        Thread.sleep(5000);  // Let the user actually see something!
        
        

        WebElement container = driver.findElement(By.className("blkContainerSblk"));
        System.out.println(container.getText());
        //WebElement searchBox = form.findElement(By.tagName("tbody"));

       // List<WebElement> rows = searchBox.findElements(By.tagName("tr"));
       // for (WebElement element : rows ) {
            //String text = driver.findElement(By.className("loudong-detail")).getText();
           // System.out.println(element.getText());
           // element.findElement(By.tagName("button")).click();
            //break;
       // }
       // System.out.println(searchBox);
       // System.out.println(searchBox.getTagName());

       // searchBox.sendKeys("ChromeDriver");
        Thread.sleep(1000);
       /* searchBox.click();
        Thread.sleep(1000);
        searchBox.sendKeys("CVE-2022-25845");
        searchBox.sendKeys(Keys.ENTER);
       //. searchBox.submit();

        Thread.sleep(5000);  // Let the user actually see something!

        driver.findElement(By.className("content-code")).click();
        Thread.sleep(5000);
        String text = driver.findElement(By.className("loudong-detail")).getText();
        System.out.println(text);*/
        System.in.read();
        //driver.quit();

        //Actions action = new Actions(driver);
       // WebElement webElement = driver.findElement(By.id("aa"));
      //  action.moveToElement(webElement).sendKeys(Keys.ENTER).build().perform();
    }

    public static void main2(String[] args) throws InterruptedException, IOException {
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        //  WebDriverManager.chromedriver().setup();

        // WebDriver driver = new ChromeDriver();

        WebDriver driver = new EdgeDriver();

        driver.get("https://www.cnnvd.org.cn/home/loophole");

        Thread.sleep(5000);  // Let the user actually see something!



        WebElement form = driver.findElement(By.className("el-form"));

        WebElement searchBox = form.findElement(By.className("el-input__inner"));
        System.out.println(searchBox);
        System.out.println(searchBox.getTagName());

        // searchBox.sendKeys("ChromeDriver");
        Thread.sleep(1000);
        searchBox.click();
        Thread.sleep(1000);
        searchBox.sendKeys("CVE-2022-25845");
        searchBox.sendKeys(Keys.ENTER);
        //. searchBox.submit();

        Thread.sleep(5000);  // Let the user actually see something!

        driver.findElement(By.className("content-code")).click();
        Thread.sleep(5000);
        String text = driver.findElement(By.className("loudong-detail")).getText();
        System.out.println(text);
        System.in.read();
        //driver.quit();
    }
}
