package com.algy.schedcore.util;

import java.util.HashSet;
import java.util.Iterator;


public class HashSetLister <K> implements Lister<K>, Iterable<K> {
    private HashSet<K> hashSet = new HashSet<K>();
    @Override
    public void add(K item) {
        hashSet.add(item);
    }

    public void clear() {
        hashSet.clear();
    }

    @Override
    public Iterator<K> iterator() {
        return hashSet.iterator();
    }

}
