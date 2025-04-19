package com.nexuscale.nexusscalemanage.util.test;

import com.nexuscale.nexusscalemanage.entity.User;

import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private final static Object lock1 = new Object();
    private final static Object lock2 = new Object();
    // 线程安全的计数器 保证打印出来的次数数字是正常的
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final AtomicInteger counter1 = new AtomicInteger(1);
    public static void deadLock(){
        Thread thread1 = new Thread(() -> {
            while (true) {
                int finalNum = counter.getAndIncrement();
                synchronized (lock1) {
                    System.out.println("第" + finalNum + "次线程1获取到了lock1");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (lock2) {
                        System.out.println("第" + finalNum + "次线程1获取到了lock2");
                    }
                }

            }

        });
        Thread thread2 = new Thread(() -> {
            while (true) {
                int finalNum = counter1.getAndIncrement();
                synchronized (lock2) {
                    System.out.println("第" + finalNum + "次线程2获取到了lock2");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (lock1) {
                        System.out.println("第" + finalNum + "次线程2获取到了lock1");
                    }
                }


            }

        });
        thread1.start();
        thread2.start();
    }
    private static boolean isAPrinted = false;
    private static int printedNum = 10;
    static class PrintLetter implements Runnable {
        private final char letter;

        PrintLetter(char letter) {
            this.letter = letter;
        }

        @Override
        public void run() {
            synchronized (lock1) {
                for (int i = 0; i < printedNum; ) {
                    if (letter == 'A' && !isAPrinted || letter == 'B' && isAPrinted) {
                        System.out.println(letter);
                        isAPrinted = !isAPrinted;
                        i++;
                        lock1.notify();
                    }else{
                        try {
                            lock1.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        }
    }
    public static void main(String[] args) {
        User user = new User();
        user.setPhoneNumber("11111111111");
        System.out.println(user.getPhoneNumber());
    }
}
