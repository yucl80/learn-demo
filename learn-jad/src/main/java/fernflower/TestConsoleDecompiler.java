package fernflower;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TestConsoleDecompiler extends ConsoleDecompiler {
    private final Map<String, ZipFile> zipFiles = new HashMap<>();

    TestConsoleDecompiler(File destination, Map<String, Object> options) {
        super(destination, options, new PrintStreamLogger(System.out));
    }

    TestConsoleDecompiler(File destination, Map<String, Object> options, IFernflowerLogger logger) {
        super(destination, options, logger);
    }

    public static void main(String[] args) {
        TestConsoleDecompiler decompiler = new TestConsoleDecompiler(new File("."),new HashMap<>());




        decompiler.decompileContext();
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        }
        else {
            ZipFile archive = zipFiles.get(file.getName());
            if (archive == null) {
                archive = new ZipFile(file);
                zipFiles.put(file.getName(), archive);
            }
            ZipEntry entry = archive.getEntry(internalPath);
            if (entry == null) throw new IOException("Entry not found: " + internalPath);
            return InterpreterUtil.getBytes(archive, entry);
        }
    }

    void close() {
        for (ZipFile file : zipFiles.values()) {
            try {
                file.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        zipFiles.clear();
    }

}
