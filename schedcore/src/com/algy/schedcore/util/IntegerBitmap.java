package com.algy.schedcore.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class IntegerBitmap <T> implements Iterable<T> {
    static final int DEFAULT_WINDOW_THRESHOLD = 4096;

    private ArrayList<T> tbl;
    // Id queue of task whose id is less than idWindowSize but which is usable slot
    Queue<Integer> availableIds; 

    private int realSize;
    private int idWindowSize;
    private int idWindowSizeLim;
    
    public IntegerBitmap (int firstWindowThreshold) {
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
    }
    
    public IntegerBitmap () {
        this(DEFAULT_WINDOW_THRESHOLD);
    }
    
    public int size () {
        return realSize;
    }
    
    public int add (T elem) {
        // invariant: tblTask.size() <= idWindowSize
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
    
    public boolean has (int id) {
        return id >= 0 && 
               id < this.idWindowSize && 
               tbl.get(id) != null;
    }
    
    public T remove (int id) {
        if (!has(id))
            return null;
        T res = tbl.get(id);
        tbl.set(id, null);
        availableIds.add(id);
        this.realSize--;
        return res;
    }
    
    public T get (int id) {
        if (!has(id)) {
            return null;
        } else {
            return tbl.get(id);
        }
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
                while (idx < tbl.size() && !has(idx)) 
                    idx++;
            }

            @Override
            public boolean hasNext() {
                return idx < tbl.size();
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