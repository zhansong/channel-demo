package com.ks3.curator;

/**
 * Created by zhansong on 2017/2/8.
 */

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorBarrierTest {


    private static final String ZK_ADDRESS = "127.0.0.1:2182";
    private static final String ZK_PATH = "/zhansong/zhansong";
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory
                .newClient(ZK_ADDRESS, retryPolicy);
        client.start();

        //创建屏障类 不同JVM需要使用相同的目录 即/DistributedBarrier
        final DistributedBarrier barrier = new DistributedBarrier(client, ZK_PATH);
        //创建屏障节点
        barrier.setBarrier();

        //启动一个线程，5000毫秒后移除屏障
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    barrier.removeBarrier();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //等待屏障移除
        barrier.waitOnBarrier();
        System.out.println("======屏障已经移除======");

        Thread.sleep(30000);
        client.close();
    }

}