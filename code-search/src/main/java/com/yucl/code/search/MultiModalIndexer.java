package com.yucl.code.search;

import org.elasticsearch.client.RestHighLevelClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiModalIndexer implements AutoCloseable {

    private final TextIndexer textIndexer;
    private final StructureIndexer structureIndexer;
    private final SemanticIndexer semanticIndexer;

    public MultiModalIndexer(RestHighLevelClient client, String openAIKey, String chromaDBUrl) {
        this.textIndexer = new TextIndexer(client);
        this.structureIndexer = new StructureIndexer(client);
        this.semanticIndexer = new SemanticIndexer(openAIKey, chromaDBUrl);
    }

    public void indexCode(String code) throws IOException {
        String id = UUID.randomUUID().toString();
        
        // 生成元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("language", "java");
        
        // 文本索引
        textIndexer.indexCode(id, code, metadata);
        
        // 结构索引
        structureIndexer.indexStructure(id, code);
        
        // 语义索引
        try {
            semanticIndexer.createIndex(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indexBatch(Map<String, String> codeMap) {
        codeMap.forEach((id, code) -> {
            try {
                indexCode(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() throws IOException {
        textIndexer.close();
        structureIndexer.close();
    }
}
