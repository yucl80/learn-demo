package com.yucl.learn.demo.nebula;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BatchTest   {
    private static final Logger log = LoggerFactory.getLogger(GraphClientExample.class);

    public static void main(String[] args) {
        NebulaPool pool = new NebulaPool();
        Session session;
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(100);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("192.168.72.137", 9669));
            Boolean initResult = pool.init(addresses, nebulaPoolConfig);
            if (!initResult) {
                log.error("pool init failed.");
                return;
            }

            session = pool.getSession("root", "nebula", false);
            {
                String createSchema = "CREATE SPACE IF NOT EXISTS call_graph(vid_type=fixed_string(40)); "
                        + "USE test;"
                        + "CREATE TAG IF NOT EXISTS pkg( body_md5 string,name string,repository_url string);"
                        + "CREATE EDGE IF NOT EXISTS has_file();"

                        + "CREATE TAG IF NOT EXISTS jar( body_md5 string,name string,version string);"
                        + "CREATE EDGE IF NOT EXISTS has_clz();"

                        + "CREATE TAG IF NOT EXISTS clz( body_md5 string,name string,type int,source_file string);"
                        + "CREATE EDGE IF NOT EXISTS implements();"
                        + "CREATE EDGE IF NOT EXISTS extends();"

                        + "CREATE EDGE IF NOT EXISTS declare_method();"

                        + "CREATE TAG IF NOT EXISTS method( body_md5 string,name string,signature string);"
                        + "CREATE EDGE IF NOT EXISTS call(callType string,lineNumber int);"

                        + "CREATE EDGE IF NOT EXISTS export_api();"
                        + "CREATE TAG IF NOT EXISTS api_method( signature string,methodName string,interfaceName string,desc string);"
                        + "CREATE TAG IF NOT EXISTS api_func( md5 string,func string,desc string);"
                        + "CREATE TAG IF NOT EXISTS api_url( md5 string,name string,desc string);"
                        + "CREATE EDGE IF NOT EXISTS impl_by();"

                        + "CREATE TAG IF NOT EXISTS file( body_md5 string,name string);"
                        ;


                ResultSet resp = session.execute(createSchema);
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                            createSchema, resp.getErrorMessage()));
                    System.exit(1);
                }
            }

            TimeUnit.SECONDS.sleep(5);
            {
                String insertVertexes = " INSERT VERTEX person(name, age) VALUES "
                        + "'Bob':('Bob', 10), "
                        + "'Lily':('Lily', 9), "
                        + "'Tom':('Tom', 10), "
                        + "'Jerry':('Jerry', 13), "
                        + "'John':('John', 11);";
                long startTime = System.currentTimeMillis();
                ResultSet resp = session.execute(insertVertexes);
                System.out.println(resp.isSucceeded() + String.valueOf(System.currentTimeMillis() - startTime));
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                            insertVertexes, resp.getErrorMessage()));
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
