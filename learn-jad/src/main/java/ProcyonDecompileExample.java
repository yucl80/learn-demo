import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ProcyonDecompileExample {
    public static void main(String[] args) {
        // 指定要反编译的 class 文件路径
        String classFilePath = "D:\\workspaces\\learn-demo\\demoproject\\target\\classes\\com\\example\\demo\\BizDto.class";

        // 反编译输出文件路径（可选）
        String outputFilePath = "Output.java";

        try {
            // 创建反编译设置
            DecompilerSettings settings = DecompilerSettings.javaDefaults();
            settings.setFlattenSwitchBlocks(false);  // 关闭 switch 块的平坦化处理
            settings.setForceExplicitTypeArguments(false);

            // 反编译结果输出到控制台
            StringBuilder decompiledCode = new StringBuilder();
            Decompiler.decompile(classFilePath, new StringBuilderOutput(decompiledCode),settings);
            System.out.println(decompiledCode);

            // 或者将反编译结果保存到文件
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)) {
                Decompiler.decompile(classFilePath, new PlainTextOutput(writer), settings);
            }

            System.out.println("Decompilation completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 自定义输出类，用于将反编译结果写入 StringBuilder
    private static class StringBuilderOutput extends PlainTextOutput {
        private final StringBuilder stringBuilder;

        public StringBuilderOutput(StringBuilder stringBuilder) {
            super();
            this.stringBuilder = stringBuilder;
        }

        @Override
        public void write(String text) {
            stringBuilder.append(text);
        }
    }
}
