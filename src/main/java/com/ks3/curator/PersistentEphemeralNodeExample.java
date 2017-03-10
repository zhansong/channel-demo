package com.ks3.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhansong on 2017/2/8.
 */
public class PersistentEphemeralNodeExample
{
    private static final String PATH = "/example/ephemeralNode";
    private static final String PATH2 = "/example/node";
    public static void main(String[] args) throws Exception
    {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
        client.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState)
            {
                System.out.println("连接状态:" + newState.name());
            }
        });
        client.start();
        PersistentNode node = new PersistentNode(client, CreateMode.PERSISTENT, false ,PATH, "临时节点".getBytes());
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
        String actualPath = node.getActualPath();
//        System.out.println("临时节点路径:" + actualPath + "- 值:" + new String(client.getData().forPath(actualPath)));
//        client.create().forPath(PATH2, "持久化节点".getBytes());
//        System.out.println("持久化节点路径:" + PATH2 + "-  值:" + new String(client.getData().forPath(PATH2)));

        CloseableUtils.closeQuietly(node);
        CloseableUtils.closeQuietly(client);
    }
}
