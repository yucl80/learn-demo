package infer;

import java.util.Map;

public class ClassContext {
    private Map<String, ClassMetadata> classMetadataMap; // 存储所有类的元数据

    public ClassContext(Map<String, ClassMetadata> classMetadataMap) {
        this.classMetadataMap = classMetadataMap;
    }

    // 查找当前类的字段类型
    public String getFieldType(String className, String fieldName) {
        ClassMetadata classMetadata = classMetadataMap.get(className);
        if (classMetadata != null) {
            return classMetadata.getFieldType(fieldName);
        }
        return null;
    }

    // 获取父类的类名
    public String getParentClass(String className) {
        ClassMetadata classMetadata = classMetadataMap.get(className);
        return (classMetadata != null) ? classMetadata.getParentClass() : null;
    }
}
