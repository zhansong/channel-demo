package com.ks3.bigdata;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by ZHANSONG on 2017/2/23.
 */
public class OOM {
    public static void main(String[] args) {
        Field unsafeField = null;
        try {
            unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            boolean t = true;
            while (t) {
                unsafe.allocateMemory(1024 * 1024 * 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
