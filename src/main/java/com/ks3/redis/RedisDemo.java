package com.ks3.redis;

import com.ks3.bean.elasticsearch.User;
import com.ks3.utils.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
* Created by ZHANSONG on 2017/3/2.
*/
@RunWith(BlockJUnit4ClassRunner.class)
public class RedisDemo {

    public ShardedJedisPoolCache cache = new ShardedJedisPoolCache();

    public static int count = 0;

    public CountDownLatch cdl = new CountDownLatch(100);

    public CyclicBarrier cb = new CyclicBarrier(100);

    @Test
    public void testCache() {
        ShardedJedisPoolCache cache = new ShardedJedisPoolCache();
        String firstKey1 = cache.set("firstKey", "1");
        System.out.println(firstKey1);
        String firstKey = cache.get("firstKey");
        System.out.println(firstKey);
        User user = new User();
        user.setId(1);
        user.setName("zhangsan");
        user.setPostDate(new Date());
        User user1 = new User();
        user1.setId(1);
        user1.setName("lisi");
        user1.setPostDate(new Date());

//        redisLock(user);
//        redisZset(user, user1);
        redisList(user, user1);
//        redisMap(user, user1);
//        redisSet(user, user1);
        try {
            cdl.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redisSet(User... users) {
        User temp = new User();
        temp.setId(2);
        temp.setName("wangwu");
        temp.setMessage("zheshiyige ceshi");
        Boolean has = cache.sismember("users1", JsonUtil.object2json(temp));
        System.out.println(has);
        has = cache.sismember("users1", "{\"id\":1,\"name\":\"zhangsan\",\"message\":null,\"postDate\":1488439275844}");
        System.out.println(has);
        cache.getConn().del("usersSet");
        Set<User> set = new HashSet<User>();
        for(User user : users) {
            cache.getConn().sadd("usersSet", JsonUtil.object2json(user));
        }
        Iterator<String> users1 = cache.smembers("users1").iterator();
        while(users1.hasNext()) {
            System.out.println(users1.next());
        }
    }

    public void redisList(User... users) {
        List<String> list = new ArrayList<String>();
        for(User user : users) {
            list.add(JsonUtil.object2json(user));
        }
        Long delCount = cache.del("users");
        System.out.println("delete length " +  delCount);
        Long userCount = cache.lpush("users", list.toArray(new String[]{}));
        System.out.println(userCount);
        //如果想把redis的list用作队列，可以使用
        User user3 = new User();
        user3.setName("wangwu");
        User user4 = new User();
        user4.setName("zhaoliu");
        cache.lpush("users", JsonUtil.object2json(user3));
        cache.rpush("users", JsonUtil.object2json(user4));
        List<String> getUsers = cache.lrange("users", 0, -1);
        for(String temp : getUsers) {
            System.out.println(temp);
            User o = JsonUtil.json2object(temp, User.class);
            System.out.println(o.toString());
        }
    }

    public void redisMap(User... user) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("haoxuesheng", JsonUtil.object2json(user[1]));
        map.put("huaixuesheng", JsonUtil.object2json(user[0]));
        cache.getConn().hmset("xuesheng", map);
        List<String> good = cache.getConn().hmget("xuesheng", "haoxuesheng");
        for(String temp : good) {
            System.out.println(temp);
        }
    }

    public void redisZset(User... user) {
        cache.del("userZset");
        cache.zadd("userZset", 1, JsonUtil.object2json(user[1]));
        cache.zadd("userZset", 2, JsonUtil.object2json(user[0]));
        Iterator<Tuple> userZset = cache.zrangeWithScores("userZset", 0, -1).iterator();
        while(userZset.hasNext()) {
            Tuple next = userZset.next();
            System.out.println(next.getScore());
            System.out.println(next.getElement());
            System.out.println(next.getBinaryElement());
        }
    }

    public void redisLock(final User user) {
        for(int i=0; i<100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String lock = "username_lock_" + user.getName();
                    String random = UUID.randomUUID().toString().replace("-", "");
                    ShardedJedis conn = cache.getConn();
                    try {
                        int numberWaiting = cb.getNumberWaiting();
                        System.out.println("当前等待线程数为:"+(numberWaiting+1));
                        cb.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        while(true) {
                            if("OK".equalsIgnoreCase(conn.set(lock, random, "NX", "PX", 10000))) {
                                System.out.println("我抢到了锁！" + Thread.currentThread().getName());
                                count++;
                                System.out.println("当前计数器值为:"+count);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String s = conn.get(lock);
                                if(random.equals(s)) {
                                    System.out.println("是我的锁，我释放了" + Thread.currentThread().getName());
                                    conn.del(lock);
                                } else {
                                    //为了避免redis集群master出错，像slave切换时会丢失一些数据，
                                    // 要判断value是否与设置的值相等，如果不相等还删除的话，会导致下一个线程又新建一个锁，逻辑上会出错
                                    System.out.println("我抢到了，但是出错了，不是我的锁" + Thread.currentThread().getName());
                                }
                                break;
                            } else {
    //                            System.out.println("我没有抢到锁！" + Thread.currentThread().getName());
                            }
                        }
                    }catch (Exception e) {

                    } finally {
                        conn.close();
                    }
                    cdl.countDown();
                }
            });
            thread.setName("NO." + i);
            thread.start();
        }
    }
}
