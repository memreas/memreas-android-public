package com.memreas.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
