package com.yucl.code.search;


import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;

public class TextIndexer {

    private RestHighLevelClient client;

    public TextIndexer(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 创建代码全文索引
     */
    public void indexCode(String id, String codeContent) {
        IndexRequest request = new IndexRequest("code_index")
                .id(id)
                .source("content", codeContent, XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            // 处理响应
            System.out.println("Index response: " + response.getResult().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


