package com.yucl;


import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        //curl 'https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2/2.0.26' --compressed
        String uri = "https://repo1.maven.org/maven2/artifact/com.buession.springboot/buession-springboot-boot";
        HttpUrl url = HttpUrl.parse(uri);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 51943));
        OkHttpClient httpClient = new OkHttpClient.Builder().proxy(proxy).build();
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            System.out.println(body);
            Document doc = Jsoup.parse(body);
            Elements talbes = doc.getElementsByTag("table");

            for (int i = 0; i < talbes.size(); i++) {
                Element table = talbes.get(i);
                if (table.hasClass("grid") && table.hasClass("versions")) {
                    Elements tbodys = table.getElementsByTag("tbody");
                    for (int n = 0; n < tbodys.size(); n++) {
                        Elements trs = tbodys.get(n).children();

                        for (int j = 0; j < trs.size(); j++) {
                            List<String> data = new ArrayList<>();
                            Elements tds =  trs.get(j).getElementsByTag("td");
                            for(int k=0;k<tds.size();k++){
                                if(!tds.get(k).hasAttr("rowspan")) {
                                    data.add(tds.get(k).text());
                                }
                            }
                            System.out.println(String.join(",", data));
                        }


                    }
                    break;

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getVersionList() {
        //curl 'https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2/2.0.26' --compressed
        String uri = "https://mvnrepository.com/artifact/com.buession.springboot/buession-springboot-boot";
        HttpUrl url = HttpUrl.parse(uri);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 51943));
        OkHttpClient httpClient = new OkHttpClient.Builder().proxy(proxy).build();
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            Document doc = Jsoup.parse(body);
            Elements talbes = doc.getElementsByTag("table");

            for (int i = 0; i < talbes.size(); i++) {
                Element table = talbes.get(i);
                if (table.hasClass("grid") && table.hasClass("versions")) {
                    Elements tbodys = table.getElementsByTag("tbody");
                    for (int n = 0; n < tbodys.size(); n++) {
                        Elements trs = tbodys.get(n).children();

                        for (int j = 0; j < trs.size(); j++) {
                            List<String> data = new ArrayList<>();
                            Elements tds =  trs.get(j).getElementsByTag("td");
                            for(int k=0;k<tds.size();k++){
                                if(!tds.get(k).hasAttr("rowspan")) {
                                    data.add(tds.get(k).text());
                                }
                            }
                            System.out.println(String.join(",", data));
                        }


                    }
                    break;

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void getJarDetail() {
        //curl 'https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2/2.0.26' --compressed
        String uri = "https://mvnrepository.com/artifact/com.buession.springboot/buession-springboot-boot/2.2.1";
        HttpUrl url = HttpUrl.parse(uri);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 51943));
        OkHttpClient httpClient = new OkHttpClient.Builder().proxy(proxy).build();
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            Document doc = Jsoup.parse(body);
            Elements talbes = doc.getElementsByTag("table");

            for (int i = 0; i < talbes.size(); i++) {
                Element table = talbes.get(i);
                String text = table.outerHtml();
                if (text.contains("License") && text.contains("Date") && text.contains("Files")) {
                    //System.out.println(table);
                    Elements trs = table.getElementsByTag("tr");
                    Elements ths = table.getElementsByTag("th");
                    Elements tds = table.getElementsByTag("td");
                    for (int j = 0; j < trs.size(); j++) {
                        System.out.println(ths.get(j).text() + " : " + tds.get(j).text());
                    }
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
