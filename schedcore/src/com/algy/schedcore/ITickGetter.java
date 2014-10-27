package com.algy.schedcore;

public interface ITickGetter {
    static ITickGetter systemTickGetter = new ITickGetter() {
        public long getTickCount() {
            return System.currentTimeMillis();
        }
    };
    public long getTickCount();
}