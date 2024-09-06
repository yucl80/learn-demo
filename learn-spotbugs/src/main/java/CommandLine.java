import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.FindBugsCommandLine;
import edu.umd.cs.findbugs.TextUICommandLine;

public class CommandLine {
    public static void main(String[] args) throws Exception {
//        FindBugs2.main(new String[]{"-quiet","-pluginList","D:\\java\\repository\\com\\h3xstream\\findsecbugs\\findsecbugs-plugin\\1.13.0\\findsecbugs-plugin-1.13.0.jar", "D:\\workspaces\\learn-demo\\demoproject\\target\\classes"});

        FindBugs2.main(new String[]{"-quiet","-pluginList","D:\\java\\repository\\com\\h3xstream\\findsecbugs\\findsecbugs-plugin\\1.13.0\\findsecbugs-plugin-1.13.0.jar", "D:\\workspaces\\learn-demo\\demoproject\\target\\classes"});
    }
}
