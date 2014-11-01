package com.algy.schedcore.util;

import java.util.Iterator;

public class IterableUtil {
    
    @SuppressWarnings("rawtypes")
    private static Iterator emptyIterator = new Iterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public void remove() {
        }
    };
    @SuppressWarnings("rawtypes")
    private static Iterable emptyIterable = new Iterable() {
        @Override
        public Iterator iterator() {
            return emptyIterator;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> getEmptyIterable() {
        return emptyIterable;
    }
}