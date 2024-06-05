package com.feiyu.ulsequence;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class SequenceTest {
    @Test
    public void test() throws IOException {
        int N = 10;
        List<Long> uids = Arrays.asList(12L, 123L, 234L, 2342L, 1638L, 1L, 234L, 2354L);
        int len = 8;
        Random random = new Random(2024);
        Properties properties = new Properties();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.AbortPolicy());
        try(InputStream is = Sequence.class.getClassLoader().getResourceAsStream("ulsequence.properties");) {
            properties.load(is);
            SequenceConfiguration cfg = SequenceConfiguration.load("node-1", properties);
            Sequence<Long> sequence = Sequence.getInstance(cfg);
            System.out.println(sequence);
            //System.exit(0);
            for (int i = 0; i < N; i++) {
                executor.execute(()->{
                    Long aLong = sequence.nexSeq(uids.get(random.nextInt() & (len - 1) ));
                    System.out.println(aLong);
                });
            }
            while (executor.getCompletedTaskCount() < N) {
                Thread.sleep(1000);
            }
            System.out.println(sequence);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("完成的任务数: " + executor.getCompletedTaskCount());
            executor.shutdown();
        }
    }
}
