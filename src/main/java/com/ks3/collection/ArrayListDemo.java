package com.ks3.collection;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ZHANSONG on 2017/3/15.
 */
public class ArrayListDemo {
    public static void main(String[] args) {
    }

    /**
     * offer方法在达到队列长度后就会返回false
     * */
    @Test
    public void offer() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        boolean offer = queue.offer("1");
        System.out.println(offer);
        offer = queue.offer("2");
        System.out.println(offer);
        offer = queue.offer("3");
        System.out.println(offer);
    }

    /**
     * add方法在达到队列长度后就会返回java.lang.IllegalStateException: Queue full
     * */
    @Test
    public void add() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        boolean offer = queue.add("1");
        System.out.println(offer);
        offer = queue.add("2");
        System.out.println(offer);
        offer = queue.add("3");
        System.out.println(offer);
    }

    /**
     * put方法在达到队列长度后会一直阻塞
     * */
    @Test
    public void put() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        try {
            queue.put("1");
            queue.put("2");
//            queue.put("3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * poll方法在队列为空时返回null
     * */
    @Test
    public void poll() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        String poll = queue.poll();

        System.out.println(poll);

    }

    /**
     * remove方法在队列为空时返回java.util.NoSuchElementException
     * */
    @Test
    public void remove() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        String poll = queue.remove();

        System.out.println(poll);

    }
    /**
     * take方法在队列为空时阻塞
     * */
    @Test
    public void take() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);

        String poll = null;
        try {
            poll = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(poll);

    }
}
