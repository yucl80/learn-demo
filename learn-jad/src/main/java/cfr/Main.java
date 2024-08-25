package cfr;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Options options =  OptionsImpl.getFactory().create( Map.of("outputdir","d:\\tmp\\src"));
        CfrDriver cfrDriver = new CfrDriver.Builder().withBuiltOptions(options).build();
        cfrDriver.analyse(Arrays.asList("D:\\workspaces\\learn-demo\\demoproject\\target\\classes\\com\\example\\demo\\BizServiceImpl.class"));
    }
}
