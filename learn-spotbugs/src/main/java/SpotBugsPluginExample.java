import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;

import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;

import java.io.File;
import java.net.URL;

public class SpotBugsPluginExample {

    public static void main(String[] args) {
        String jarFilePath = "path/to/your/file.jar";
        String pluginPath = "path/to/findsecbugs-plugin.jar"; // FindSecBugs插件的路径

        // 创建SpotBugs项目
        Project project = new Project();
        project.addFile(jarFilePath);

        // 创建FindBugs2对象
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(project);

        try {
            // 设置用户首选项
            UserPreferences preferences = UserPreferences.createDefaultUserPreferences();
            findBugs.setUserPreferences(preferences);

            // 使用PluginLoader手动加载插件
            URL pluginUrl = new File(pluginPath).toURI().toURL();


            // 执行SpotBugs分析
            findBugs.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

