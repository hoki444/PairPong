package com.algy.schedcore.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class IntegerBitmap <T> implements Iterable<T> {
    public static final int DEFAULT_WINDOW_THRESHOLD = 4096;

    private ArrayList<T> tbl;
    // Id queue of task whose id is less than idWindowSize but which is usable slot
    private Queue<Integer> availableIds; 

    private int realSize;
    private int idWindowSize;
    private int idWindowSizeLim;
    
    private T sentinel;
    
    public IntegerBitmap (int firstWindowThreshold, T sentinel) {
        // invariant: tbl.size() <= idWindowSize
        this.availableIds = new LinkedList<Integer>();
        this.tbl = new ArrayList<T>(firstWindowThreshold);
        for (int i = 0; i < firstWindowThreshold; i++) {
            // fill initially with null for the sake of ease of managing table
            this.tbl.add(null);
        }
        this.realSize = 0;
        this.idWindowSize = 0;
        this.idWindowSizeLim = firstWindowThreshold;
        this.sentinel = sentinel;
    }
    
    public IntegerBitmap (T sentinel) {
        this(DEFAULT_WINDOW_THRESHOLD, sentinel);
    }
    
    public synchronized int size () {
        return realSize;
    }
    
    public synchronized int add (T elem) {
        // invariant: tbl.size() <= idWindowSize
        int id;
        if (this.idWindowSize >= this.idWindowSizeLim) {
            if (this.availableIds.isEmpty()) {
                this.idWindowSizeLim *= 2;
                int len = (int)(this.idWindowSizeLim - tbl.size());
                for (int i = 0; i < len; i++) {
                    tbl.add(null);
                }
                id = this.idWindowSize++;
            } else {
                id = this.availableIds.poll();
            }
        } else {
            id = this.idWindowSize++;
        }
        this.realSize++;
        tbl.set(id, elem);
        return id;
    }
    
    private boolean _has (int id) {
        if (id >= 0 && id < this.idWindowSize) {
           T cell = tbl.get(id);
           if (cell != null && cell != sentinel) {
               return true;
           }
        }
        return false;
    }
    
    public synchronized boolean has (int id) {
        return _has(id);
    }
    
    public synchronized T remove (int id) {
        if (!_has(id))
            return null;
        T res = tbl.get(id);
        tbl.set(id, sentinel);
        if (sentinel == null) {
            availableIds.add(id);
        }
        this.realSize--;
        return res;
    }
    
    public synchronized T get (int id) {
        if (!_has(id)) {
            return null;
        } else {
            return tbl.get(id);
        }
    }
    
    private synchronized boolean isInsideTblBorder(int idx) {
        return idx < tbl.size();
    }
    
    public synchronized boolean releaseId (int id) {
        if (sentinel == null) {
            return true;
        }

        if (!_has(id))
            return false;
        T cell = tbl.get(id);
        if (cell != sentinel)
            return false;
        tbl.set(id, null);
        availableIds.add(id);
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        // XXX: Inefficient iteration, IMPROVEME
        return new Iterator<T>() {
            int idx = 0;
            {
                readyIdx();
            }
            private void readyIdx( ) {
                while (isInsideTblBorder(idx) && !has(idx))
                    idx++;
            }

            @Override
            public boolean hasNext() {
                return isInsideTblBorder(idx);
            }

            @Override
            public T next() {
                T result = get(idx++);
                readyIdx();
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}