package com.yucl.code.search;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文本索引器
 */
public class TextIndexer {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "code_index";

    /**
     * 构造函数
     *
     * @param client Elasticsearch客户端
     */
    public TextIndexer(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 索引代码
     *
     * @param id         文档ID
     * @param codeContent 代码内容
     * @param metadata    元数据
     * @throws IOException 索引异常
     */
    public void indexCode(String id, String codeContent, Map<String, Object> metadata) throws IOException {
        IndexRequest request = new IndexRequest(INDEX_NAME)
                .id(id)
                .source(XContentType.JSON,
                        "content", codeContent,
                        "metadata", metadata);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Index response: " + response.getResult().name());
    }

    /**
     * 搜索代码
     *
     * @param query 搜索查询
     * @return 搜索结果
     * @throws IOException 搜索异常
     */
    public List<SearchResult> search(String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 使用多字段匹配和模糊查询
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(query)
                .field("content", 2.0f)
                .field("metadata.*")
                .fuzziness(Fuzziness.AUTO));

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        List<SearchResult> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            results.add(new SearchResult(
                    hit.getId(),
                    hit.getScore(),
                    hit.getSourceAsMap()
            ));
        }

        return results;
    }

    /**
     * 关闭客户端
     *
     * @throws IOException 关闭异常
     */
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 搜索结果
     */
    public static class SearchResult {
        private final String id;
        private final float score;
        private final Map<String, Object> source;

        public SearchResult(String id, float score, Map<String, Object> source) {
            this.id = id;
            this.score = score;
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public float getScore() {
            return score;
        }

        public Map<String, Object> getSource() {
            return source;
        }
    }
}
