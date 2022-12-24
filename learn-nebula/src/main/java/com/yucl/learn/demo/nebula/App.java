package com.yucl.learn.demo.nebula;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(20);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("192.168.72.137", 9669),
                    new HostAddress("192.168.72.137", 9670));
            NebulaPool pool = new NebulaPool();
            pool.init(addresses, nebulaPoolConfig);
            Session session = pool.getSession("root", "nebula", false);
            session.execute("SHOW HOSTS;");
            session.release();
            pool.close();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }
}
