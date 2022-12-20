package com.yucl.learn.demo.nebula;

import com.google.common.collect.Lists;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GraphMultiThreadWriteExample {
    private static final Logger logger = LoggerFactory.getLogger(GraphMultiThreadWriteExample.class);
    private static final NebulaPool pool = new NebulaPool();
    private static volatile boolean isRunning = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        String dataFile =  Thread.currentThread().getContextClassLoader().getResource("vertexs.txt").getFile();
        int dataType = 1;
        int numThread = 3;
        int batchNum = 100;
        if (args.length > 3) {
            dataFile = args[0].trim();
            dataType = NumberUtils.toInt(args[1]);
            numThread = NumberUtils.toInt(args[2]);
            batchNum = NumberUtils.toInt(args[3]);
        }
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10000);
        ExecutorService executor = Executors.newFixedThreadPool(numThread+1);
        Runtime.getRuntime().addShutdownHook(getExitHandler());
        initPool();

        CountDownLatch latch = new CountDownLatch(numThread);
        List<WorkThread> workThreads = Lists.newArrayList();
        try {
            for (int k = 0; k < numThread; k++) {
                WorkThread workThread = new WorkThread(queue, pool, batchNum, dataType, latch);
                workThreads.add(workThread);
                executor.execute(workThread);
            }
        } catch (Exception e) {
            logger.error("添加工作线程失败", e);
        }
        isRunning = true;
        Runnable statThread = () -> {
            while (isRunning) {
                long total = workThreads.stream().map(x -> x.getProcessNum()).reduce(0L, (a, b) -> a + b);
                logger.info("已经处理了{}条", total);
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
//                    logger.error(e.getMessage(), e);
                }
            }
        };
        executor.execute(statThread);

        executor.shutdown();

        long start = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(new File(dataFile)));
        String temp = null;
        while ((temp = br.readLine()) != null) {
            if (temp.startsWith("#")) {
                continue;
            }
            queue.put(temp);
        }


        for (int i = 0; i < numThread; i++) {
            queue.add("QUIT");
        }
        isRunning = false;

        latch.await();
        for (WorkThread workThread : workThreads) {
            workThread.close();
        }
        pool.close();
        executor.shutdownNow();

        long totalNum = workThreads.stream().map(x -> x.getProcessNum()).reduce(0L, (a, b) -> a + b);
        logger.info("全部{}线程执行完毕,总共处理{}条, 总耗时{}ms", numThread, totalNum, (System.currentTimeMillis() - start));
    }

    private static void initPool() {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(100);
            List<HostAddress> addresses = Lists.newArrayList();
            addresses.add(new HostAddress("192.168.72.137", 9669));
           /* addresses.add(new HostAddress("172.25.21.19", 9669));
            addresses.add(new HostAddress("172.25.21.22", 9669));*/
            pool.init(addresses, nebulaPoolConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static Thread getExitHandler() {
        return new Thread() {
            @Override
            public void run() {
                System.out.println("程序退出");
            }
        };
    }
}

class WorkThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(WorkThread.class);
    private static int counter = 0;
    private final int id = ++counter;

    private final BlockingQueue<String> queue;
    private final NebulaPool pool;
    private final int batchNum;
    private final int dataType;
    private final CountDownLatch latch;

    private final String QUIT_STR = "QUIT";
    private final AtomicLong sum = new AtomicLong(0);
    ;
    private Session session;

    public WorkThread(BlockingQueue<String> queue, NebulaPool pool, int batchNum, int dataType, CountDownLatch latch) throws NotValidConnectionException, IOErrorException, AuthFailedException, UnsupportedEncodingException, ClientServerIncompatibleException {
        this.queue = queue;
        this.pool = pool;
        this.batchNum = batchNum;
        this.dataType = dataType;
        this.latch = latch;

        session = pool.getSession("root", "nebula", true);
        session.execute("use friendster;");
    }

    @Override
    public void run() {
        if (dataType == 1) {
            doVertexWork();
        } else {
            doEdgeWork();
        }
        latch.countDown();
    }

    private void doVertexWork() {
        try {
            String insertVertexes = "INSERT VERTEX person() VALUES ";
            String rowStrFormat = ", %s:()";
            StringBuffer sb = new StringBuffer();

            for (; ; ) {
                String line = queue.take();
                if (QUIT_STR.equalsIgnoreCase(line)) {
                    break;
                }
                sb.append(String.format(rowStrFormat, line));
                sum.addAndGet(1);
                if (sum.get() % batchNum == 0) {
                    String sql = insertVertexes + sb.substring(1) + ";";
                    sb.setLength(0);
                    insertData(sql);
                }
            }
            if (sum.get() % batchNum != 0) {
                String sql = insertVertexes + sb.substring(1) + ";";
                insertData(sql);
                logger.info(String.format("线程%s共处理了%s条", id, sum.get()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void doEdgeWork() {
        try {
            String insertVertexes = "INSERT EDGE friend() VALUES ";
            String rowStrFormat = ", %s->%s:()";
            StringBuffer sb = new StringBuffer();

            for (; ; ) {
                String line = queue.take();
                if (QUIT_STR.equalsIgnoreCase(line)) {
                    break;
                }
                String[] cols = line.split("\\s+");
                sb.append(String.format(rowStrFormat, cols[0], cols[1]));
                sum.addAndGet(1);
                if (sum.get() % batchNum == 0) {
                    String sql = insertVertexes + sb.substring(1) + ";";
                    sb.setLength(0);
                    insertData(sql);
                }
            }
            if (sum.get() % batchNum != 0) {
                String sql = insertVertexes + sb.substring(1) + ";";
                insertData(sql);
                logger.info(String.format("线程%s共处理了%s条", id, sum.get()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void insertData(String sql) {
        try {
            ResultSet resp = session.execute(sql);
            if (!resp.isSucceeded()) {
                logger.error(String.format("Execute: `%s', failed: %s", sql, resp.getErrorMessage()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void close() {
        if (session != null) {
            session.release();
        }
    }

    public long getProcessNum() {
        return sum.get();
    }
}