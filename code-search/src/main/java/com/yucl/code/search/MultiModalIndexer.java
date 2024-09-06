package com.yucl.code.search;

import java.util.HashMap;
import java.util.Map;

public class MultiModalIndexer {

    private TextIndexer textIndexer;
    private StructureIndexer structureIndexer;
    private SemanticIndexer semanticIndexer;

    public MultiModalIndexer(TextIndexer textIndexer, StructureIndexer structureIndexer, SemanticIndexer semanticIndexer) {
        this.textIndexer = textIndexer;
        this.structureIndexer = structureIndexer;
        this.semanticIndexer = semanticIndexer;
    }

    public void indexCode(String id, String codeContent) {
        // 生成文本、结构和语义索引
        String textIndex = codeContent;
        String structureIndex = structureIndexer.generateStructureIndex(codeContent);
        float[] semanticIndex = semanticIndexer.generateSemanticEmbedding(codeContent);

        // 存储索引信息
        Map<String, Object> indexData = new HashMap<>();
        indexData.put("text", textIndex);
        indexData.put("structure", structureIndex);
        indexData.put("semantic", semanticIndex);

        // 这里可以将 indexData 存储到数据库或索引引擎中
    }
}


