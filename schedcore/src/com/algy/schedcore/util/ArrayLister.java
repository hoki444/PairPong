package com.algy.schedcore.util;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayLister <K> implements Iterable<K>, Lister<K> {
    private ArrayList<K> arr = new ArrayList<K>();
    public void add(K item) {
        arr.add(item);
    }
    public void clear() {
        arr.clear();
    }

    @Override
    public Iterator<K> iterator() {
        return arr.iterator();
    }
}
