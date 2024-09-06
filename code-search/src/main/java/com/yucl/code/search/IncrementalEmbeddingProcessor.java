package com.yucl.code.search;

import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncrementalEmbeddingProcessor {

    private Map<String, String> previousHashes = new HashMap<>();

    // 生成代码块的哈希值
    private String generateHash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(0xff & b));
        }
        return hexString.toString();
    }

    // 检查代码块是否发生变化
    private boolean hasBlockChanged(String block, String blockName) throws Exception {
        String currentHash = generateHash(block);
        String previousHash = previousHashes.get(blockName);
        if (previousHash == null || !previousHash.equals(currentHash)) {
            previousHashes.put(blockName, currentHash);
            return true;
        }
        return false;
    }

    // 增量处理方法
    public void processBlocksIncrementally(List<String> blocks, String methodName) throws Exception {
        for (int i = 0; i < blocks.size(); i++) {
            String blockName = methodName + "_block_" + i;
            if (hasBlockChanged(blocks.get(i), blockName)) {
                // 只对变化的块生成嵌入
                List<float[]> embeddings = generateEmbeddings(blocks.get(i));
                // 存储或处理嵌入
            }
        }
    }

    private List<float[]> generateEmbeddings(String code) {
        // 生成嵌入的实现（同前）
        return new ArrayList<>();
    }

    // 读取存储的哈希值以实现持久化的增量处理
    private void loadPreviousHashes() {
        // 实现从磁盘或数据库加载哈希值
    }

    // 存储当前哈希值以便后续处理
    private void storeCurrentHashes() {
        // 实现将哈希值保存到磁盘或数据库
    }
}
