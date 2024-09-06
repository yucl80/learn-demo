import edu.umd.cs.findbugs.*;

import edu.umd.cs.findbugs.config.UserPreferences;


import java.io.File;
import java.net.URL;

public class SpotBugsManualPluginLoader {

    public static void main(String[] args) {
        String jarFilePath = "path/to/your/file.jar";

        Project project = new Project();
        project.addFile(jarFilePath);

        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(project);

        try {
            // 创建用户首选项
            UserPreferences preferences = UserPreferences.createDefaultUserPreferences();
            findBugs.setUserPreferences(preferences);

            // 手动加载插件

            PluginLoader pluginLoader =PluginLoader.getPluginLoader(new URL("file:///D:\\java\\repository\\com\\h3xstream\\findsecbugs\\findsecbugs-plugin\\1.13.0\\findsecbugs-plugin-1.13.0.jar"),Thread.currentThread().getContextClassLoader(),true,false);

            Plugin plugin = pluginLoader.loadPlugin();

            System.out.println(plugin);

//            Plugin plugin = new Plugin("com.h3xstream.findsecbugs",null,null,pluginLoader,true,false );

            DetectorFactoryCollection.resetInstance(DetectorFactoryCollection.instance());

            System.out.println(DetectorFactoryCollection.getFindBugsHome());

            Plugin findSecBugsPlugin = DetectorFactoryCollection.instance().getPluginById("com.h3xstream.findsecbugs");
            System.out.println(findSecBugsPlugin);



            // 执行分析
//            findBugs.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
