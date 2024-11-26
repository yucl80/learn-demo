package com.yucl.code.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiModalSearcher {

    private TextIndexer textIndexer;
    private StructureIndexer structureIndexer;
    private SemanticIndexer semanticIndexer;

    public MultiModalSearcher(TextIndexer textIndexer, StructureIndexer structureIndexer, SemanticIndexer semanticIndexer) {
        this.textIndexer = textIndexer;
        this.structureIndexer = structureIndexer;
        this.semanticIndexer = semanticIndexer;
    }

    public List<SearchResult> search(String query) throws IOException {
        // 生成多模态查询表示
        String queryText = query;
        String queryStructure = generateQueryStructure(query);
        float[] querySemantic = generateQuerySemantic(query);

        // 使用不同模态进行搜索
        List<SearchResult> textResults = textIndexer.search(queryText);
        List<SearchResult> structureResults = structureIndexer.search(queryStructure);
        List<SearchResult> semanticResults = semanticIndexer.search(querySemantic);

        // 合并结果
        return mergeResults(textResults, structureResults, semanticResults);
    }

    private String generateQueryStructure(String query) {
        // 使用正则表达式提取查询中的结构信息
        StringBuilder structure = new StringBuilder();

        // 提取类名相关
        if (query.matches(".*\\b(class|interface)\\b.*")) {
            structure.append("Type: ").append(query.contains("class") ? "Class" : "Interface").append("\n");
        }

        // 提取方法相关
        if (query.matches(".*\\b(method|function)\\b.*")) {
            structure.append("Member: Method\n");
        }

        // 提取字段相关
        if (query.matches(".*\\b(field|variable|property)\\b.*")) {
            structure.append("Member: Field\n");
        }

        return structure.toString();
    }

    private float[] generateQuerySemantic(String query) {
        return semanticIndexer.generateSemanticEmbedding(query);
    }

    private List<SearchResult> mergeResults(List<SearchResult> textResults, 
                                          List<SearchResult> structureResults, 
                                          List<SearchResult> semanticResults) {
        // 使用Map来存储合并后的结果，键为文档ID
        Map<String, SearchResult> mergedResults = new HashMap<>();

        // 合并文本搜索结果
        for (SearchResult result : textResults) {
            mergedResults.put(result.getId(), result);
        }

        // 合并结构搜索结果
        for (SearchResult result : structureResults) {
            if (mergedResults.containsKey(result.getId())) {
                mergedResults.get(result.getId()).updateScore(result.getScore());
            } else {
                mergedResults.put(result.getId(), result);
            }
        }

        // 合并语义搜索结果
        for (SearchResult result : semanticResults) {
            if (mergedResults.containsKey(result.getId())) {
                mergedResults.get(result.getId()).updateScore(result.getScore());
            } else {
                mergedResults.put(result.getId(), result);
            }
        }

        // 将结果转换为列表并排序
        List<SearchResult> finalResults = new ArrayList<>(mergedResults.values());
        Collections.sort(finalResults);
        return finalResults;
    }
}

class SearchResult implements Comparable<SearchResult> {
    private String id;
    private double score;

    public SearchResult(String id, double score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public void updateScore(double score) {
        this.score += score;
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score);
    }
}


