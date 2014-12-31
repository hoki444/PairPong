package com.algy.schedcore;

public interface TickGetter {
    public static TickGetter systemTickGetter = new TickGetter() {
        public long getTime() {
            return System.currentTimeMillis();
        }
    };
    public static TickGetter systemNanoTickGetter = new TickGetter() {
           public long getTime() {
               return System.nanoTime();
           }
    };
    public static TickGetter systemMicroTickGetter = new TickGetter() {
           public long getTime() {
               return System.nanoTime() / 1000L;
           }
    };
 

    public long getTime();
}