package procyon;

import org.jboss.windup.decompiler.api.Decompiler;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;
import org.jboss.windup.decompiler.procyon.ProcyonDecompiler;

import java.nio.file.Paths;

public class TestDecompiler {
    public static void main(String[] args) {
        Decompiler decompiler = new ProcyonDecompiler();
        decompiler.decompileClassFile(Paths.get("d:/tmp"), Paths.get("D:\\workspaces\\learn-demo\\demoproject\\target\\classes\\com\\example\\demo\\BizServiceImpl.class"), Paths.get("d:/tmp/src"));
        decompiler.close();
    }
}
