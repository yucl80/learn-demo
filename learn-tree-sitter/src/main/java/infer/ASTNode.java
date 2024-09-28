package infer;

import java.util.List;

// 简单的ASTNode类
public class ASTNode {
    private String type;
    private String text;
    private List<ASTNode> children;

    public ASTNode(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public ASTNode getChild(String name) {
        // 查找并返回匹配的子节点 (这里假设有名称字段作为标识符)
        return null;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public List<ASTNode> getChildren(String name) {
        return children;
    }
}


