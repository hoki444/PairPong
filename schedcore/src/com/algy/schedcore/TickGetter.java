package com.algy.schedcore;

public interface TickGetter {
    public static TickGetter systemTickGetter = new TickGetter() {
        public long getTickCount() {
            return System.currentTimeMillis();
        }
    };
    public static TickGetter systemNanoTickGetter = new TickGetter() {
           public long getTickCount() {
               return System.nanoTime();
           }
    };
 

    public long getTickCount();
}