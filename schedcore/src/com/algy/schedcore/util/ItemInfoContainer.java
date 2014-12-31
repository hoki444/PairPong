package com.algy.schedcore.util;

import java.util.HashMap;
import java.util.Iterator;

import com.algy.schedcore.TypeConflictError;

class ItemInfoContainer <K> implements Iterable<K> {
    /**
     * This class is responsible for managing 
     * hierarchy of components and fast access to component
     * 
     */
    private HashMap<Class<? extends K>, ItemInfo<K>> compMap;
    private Class<K> kClass;
    
    public ItemInfoContainer (Class<K> kClass) {
        this.compMap = new HashMap<Class<? extends K>, ItemInfo<K>>();
        this.kClass = kClass;
    }
    
    @SuppressWarnings("unchecked")
    public void add(K comp) {
        Class<K> clazz = (Class<K>) comp.getClass();
        
        if (!kClass.isAssignableFrom(comp.getClass())) {
            throw new TypeConflictError(comp.getClass() + 
                                        ", class of given argument, is not derived from " + 
                                        kClass.getClass()); 
        }
        
        if (compMap.get(clazz) != null) {
            throw new TypeConflictError("Tried to add component of " + comp.getClass().toString() +
                                        ", but already have a component of the same or its derived class");
        } else {
            ItemInfo<K> iterItemInfo = ItemInfo.makeLeaf(clazz, comp);
            compMap.put(clazz, iterItemInfo);

            Class<K> parentClass = (Class<K>) clazz.getSuperclass();
            while (parentClass != null) {
                ItemInfo<K> parentInfo;
                if ((parentInfo = compMap.get(parentClass)) != null) {
                    if (parentInfo.residingComp != null) {
                        throw new TypeConflictError("Tried to add component of " + comp.getClass().toString() +
                                                        ", but already have a component of its super class");
                    }
                    parentInfo.addChild(iterItemInfo);
                    break;
                } else {
                    iterItemInfo = ItemInfo.makeInner(parentClass, iterItemInfo);
                    compMap.put(parentClass, iterItemInfo);
                    if (parentClass.equals(kClass)) break;
                }
                parentClass = (Class<K>) parentClass.getSuperclass();
            }
            if (parentClass == null) {
                throw new TypeConflictError(comp.getClass() + 
                                            ", class of given argument, is not extended from " + 
                                            kClass.getClass()); 
            }
        }
    }

    public Iterable<K> remove (Class<? extends K> clazz) {
        if (!kClass.isAssignableFrom(clazz)) {
            throw new TypeConflictError(clazz  + 
                                        ", given argument, is not a class that derived from " + 
                                        kClass.getClass()); 
        }
        
        final ItemInfo<K> info;
        if ((info = compMap.get(clazz)) == null) {
            return IterableUtil.getEmptyIterable();
        } else {
            for (ItemInfo<K> subinfo : info)  {
                compMap.remove(subinfo.compClass);
            }
            ItemInfo<K> parentInfo = info.remove();
            if (parentInfo != null) {
                while (parentInfo != null) {
                    if (parentInfo.descendantCnt == 1) {
                        compMap.remove(parentInfo.compClass);
                        parentInfo = parentInfo.remove();
                    } else
                        break;
                }
            }
            return info.components();
        }
    }

    public boolean has (Class<? extends K> clazz) {
        return compMap.containsKey(clazz);
    }

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
    public <T extends K> Iterable<T> filter(final Class<? extends T> clazz) {
        ItemInfo<T> info = (ItemInfo<T>)compMap.get(clazz);
        
        if (info != null) {
            return info.components();
        } else {
            return (Iterable<T>) emptyIterable;
        }
    }
    

    @Override
    public Iterator<K> iterator() {
        return filter(kClass).iterator();
    }
}