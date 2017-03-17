package com.ks3.concurrent;

import java.util.concurrent.*;

/**
 * Created by ZHANSONG on 2017/3/17.
 */
public class ThreadPoolDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //最大值线程，如果当前队列以满，会创建新线程去执行
        ExecutorService executorService = Executors.newCachedThreadPool();
        //定长线程池,如果大于当前队列的大小,会等待其他线程执行完毕
        ExecutorService executorService1 = Executors.newFixedThreadPool(100);
        //定时器线程池,可以按照一定规则轮询执行
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        //单线程池,只有一个线程去执行,能够保证提交的顺序
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();


        Future<String> submit = executorService1.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "我是线程返回值";
            }
        });

        String result = submit.get();

        System.out.println("返回值为:"+result);

        try {
            //可以设定获取返回值的时间，如果超出该时间，则直接返回
            result = submit.get(10000l, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Future<?> future = executorService1.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("我是没有返回值的线程");
            }
        });


        executorService1.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
