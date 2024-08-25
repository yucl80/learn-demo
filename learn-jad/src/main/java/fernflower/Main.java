package fernflower;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Object> options = new HashMap<>();
        options.put(IFernflowerPreferences.LOG_LEVEL, "warn");
//        options.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
//        options.put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1");
//        options.put(IFernflowerPreferences.REMOVE_BRIDGE, "1");
//        options.put(IFernflowerPreferences.LITERALS_AS_IS, "1");
//        options.put(IFernflowerPreferences.UNIT_TEST_MODE, "1");

        org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler decompiler = new ConsoleDecompiler(new File("./javasrc"), options);
        decompiler.addSpace(new File("D:\\workspaces\\learn-demo\\demoproject\\target\\classes\\com\\example\\demo\\BizServiceImpl.class"), true);


        decompiler.decompileContext();
    }
}
