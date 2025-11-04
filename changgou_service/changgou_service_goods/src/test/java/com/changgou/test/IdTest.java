package com.changgou.test;

import com.changgou.util.IdWorker;

public class IdTest {
    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker(1, 1);
        long id = idWorker.nextId();
        System.out.println(id);
    }
}
