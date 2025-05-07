package com.nexuscale.nexusscalemanage.jvm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class ScheduleThreadPoolExecutorExample {

    private void testScheduledThreadPoolExecutor(){
        // 创建一个包含 2 个线程的 ScheduledThreadPoolExecutor
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        // 延迟 3 秒后执行任务
        ScheduledFuture<?> future = executor.schedule(() -> {
            System.out.println("Delayed task executed.");
        }, 3, TimeUnit.SECONDS);

        // 初始延迟 1 秒，然后每隔 2 秒执行一次任务
        executor.scheduleAtFixedRate(() -> {
            System.out.println("Periodic task executed.");
        }, 1, 2, TimeUnit.SECONDS);

        // 初始延迟 2 秒，每次任务执行完成后，等待 3 秒再执行下一次任务
        executor.scheduleWithFixedDelay(() -> {
            System.out.println("Fixed delay task executed.");
        }, 2, 3, TimeUnit.SECONDS);

        // 关闭线程池
//        try {
//            // 等待 10 秒后关闭线程池
//            executor.awaitTermination(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            executor.shutdown();
//        }
    }
    private static class CardInfo{
        BigDecimal price = new BigDecimal("0.0");
        String name = "Card";
        int age = 5;
        Date birthday = new Date();
        public void m(){};
    }
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50,new ThreadPoolExecutor.DiscardOldestPolicy());

    private static void modelFit(){
        List<CardInfo> taskList = getAllCardInfo();
        taskList.forEach(task -> {
            executor.scheduleWithFixedDelay(task::m, 2, 3, TimeUnit.SECONDS);
        });
    }

    private static List<CardInfo> getAllCardInfo(){
        List<CardInfo> taskList = new ArrayList<CardInfo>();

        for (int i = 0; i < 100; i++) {
            CardInfo cardInfo = new CardInfo();
            taskList.add(cardInfo);
        }
        return taskList;
    }

    public static void main(String[] args) throws InterruptedException {
        executor.setMaximumPoolSize(50);
        for(;;){
            modelFit();
            Thread.sleep(1000);  // 每隔1s执行依次modelFit()，一次执行获取100个对象，每个对象依次交给线程池周期性隔3s执行一次，线程池处理的对象数量逐渐增加，直至最后溢出（先是老年代满，频繁全量GC 然后就是内存溢出）
        }
    }
}