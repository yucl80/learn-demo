package infer;

import java.util.Map;

public class ClassMetadata {
    private String className;
    private String parentClassName;
    private Map<String, String> fields; // 字段名 -> 字段类型的映射

    public ClassMetadata(String className, String parentClassName, Map<String, String> fields) {
        this.className = className;
        this.parentClassName = parentClassName;
        this.fields = fields;
    }

    // 获取字段的类型
    public String getFieldType(String fieldName) {
        return fields.get(fieldName);
    }

    // 获取父类的类名
    public String getParentClass() {
        return parentClassName;
    }
}
