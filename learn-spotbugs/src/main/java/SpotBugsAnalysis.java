import com.h3xstream.findsecbugs.FindSecBugsGlobalConfig;

import edu.umd.cs.findbugs.*;  // SpotBugs 的核心包
import edu.umd.cs.findbugs.config.UserPreferences;
import edu.umd.cs.findbugs.filter.BugMatcher;
import edu.umd.cs.findbugs.filter.Matcher;
import edu.umd.cs.findbugs.xml.XMLOutput;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpotBugsAnalysis {
    public static void main(String[] args) {
        try {
//            FindSecBugsGlobalConfig.getInstance();//.setCustomConfigFile(SpotBugsAnalysis.class.getResource("/findbugs-security-plugin.xml"));
            try {
                Plugin.addCustomPlugin(new URI("com.h3xstream.findsecbugs"),FindSecBugsGlobalConfig.getInstance().getClass().getClassLoader());
            } catch (PluginException e) {
                e.printStackTrace();
            }

            // 创建 Project 实例
            Project project = new Project();


            // 添加要分析的 Java .class 文件或目录
            project.addFile("D:\\workspaces\\learn-demo\\demoproject\\target\\classes");  // 修改为实际的 class 文件路径

            // 添加分析依赖的 jar 文件
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-jdbc\\5.3.23\\spring-jdbc-5.3.23.jar"); // 修改为实际的依赖 jar 文件路径
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-core\\5.3.23\\spring-core-5.3.23.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-web\\5.3.23\\spring-web-5.3.23.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-webmvc\\5.3.23\\spring-webmvc-5.3.23.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\mybatis\\mybatis\\3.5.11\\mybatis-3.5.11.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\data\\spring-data-jpa\\2.7.3\\spring-data-jpa-2.7.3.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\boot\\spring-boot\\2.7.4\\spring-boot-2.7.4.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\data\\spring-data-commons\\2.7.3\\spring-data-commons-2.7.3.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-context\\5.3.23\\spring-context-5.3.23.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-beans\\5.3.23\\spring-beans-5.3.23.jar");
            project.addAuxClasspathEntry("D:\\java\\repository\\org\\springframework\\spring-tx\\5.3.23\\spring-tx-5.3.23.jar");
            // 创建 BugCollector 实例用于收集 Bug 信息
            BugCollectionBugReporter reporter = new BugCollectionBugReporter(project);
            reporter.setPriorityThreshold(Priorities.NORMAL_PRIORITY);

            // 配置 UserPreferences（可以根据需要修改分析选项）
            UserPreferences userPreferences = UserPreferences.createDefaultUserPreferences();
            userPreferences.setEffort(UserPreferences.EFFORT_MAX);
//            userPreferences.setCustomPlugins(Map.of("D:\\java\\repository\\com\\h3xstream\\findsecbugs\\findsecbugs-plugin\\1.13.0\\findsecbugs-plugin-1.13.0.jar", true));


            // 初始化 SpotBugs 引擎
            try(FindBugs2 findBugs = new FindBugs2();) {
                findBugs.setProject(project);
                findBugs.setBugReporter(reporter);
                findBugs.setUserPreferences(userPreferences);
                DetectorFactoryCollection detectorFactoryCollection = DetectorFactoryCollection.instance();

                Plugin plugin =  detectorFactoryCollection.getPluginById("com.h3xstream.findsecbugs");
                if(plugin != null) {
                    System.out.println("Plugin loaded: "+plugin.getDetailedDescription());
                }else{
                    System.out.println("Plugin not loaded");
                }

                findBugs.setDetectorFactoryCollection(detectorFactoryCollection);

                // 打印所有加载的插件，确认 FindSecBugs 是否加载
                System.out.println("Loaded plugins:");
                for (DetectorFactory factory : detectorFactoryCollection.getFactories()) {
                    System.out.println("Plugin: " + factory.getFullName()+ factory.getPlugin().getDetailedDescription());
                }

                findBugs.execute();

                // 获取 Bug 信息
                BugCollection bugCollection = reporter.getBugCollection();
                List<BugInstance> bugs = new ArrayList<>(bugCollection.getCollection());

                // 打印 Bug 信息
                for (BugInstance bug : bugs) {
                    System.out.println(bug.getBugPattern().getType() + ": " + bug.getMessage());
                }
            }


        } catch (Exception e) {
           // e.printStackTrace();
        }
    }
}
