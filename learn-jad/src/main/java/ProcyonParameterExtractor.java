import com.strobel.assembler.metadata.*;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.*;

import java.util.List;

public class ProcyonParameterExtractor {
    public static void main(String[] args) {
        String className = "ProcyonParameterExtractor"; // 你要分析的类名
        String methodName = "main"; // 你要获取参数名的方法名

        try {
            // 加载类元数据
            MetadataSystem metadataSystem = new MetadataSystem();
            TypeReference typeReference = metadataSystem.lookupType(className);
            TypeDefinition typeDefinition = typeReference.resolve();

            // 遍历类中的所有方法
            for (MethodDefinition method : typeDefinition.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    // 获取方法的参数
                    List<ParameterDefinition> parameters = method.getParameters();
                    for (ParameterDefinition parameter : parameters) {
                        System.out.println("Parameter: " + parameter.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
