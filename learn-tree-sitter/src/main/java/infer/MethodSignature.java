package infer;

import java.util.List;
import java.util.Objects;

// 定义方法签名类
class MethodSignature {
    private String methodName;
    private List<String> argumentTypes;

    public MethodSignature(String methodName, List<String> argumentTypes) {
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodSignature)) return false;
        MethodSignature other = (MethodSignature) obj;
        return Objects.equals(methodName, other.methodName) &&
                Objects.equals(argumentTypes, other.argumentTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, argumentTypes);
    }
}