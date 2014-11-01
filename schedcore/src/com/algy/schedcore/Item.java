package com.algy.schedcore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

class ItemInfo <K> implements Iterable<ItemInfo<K>>{
    /*
     * Invariant1: descendantCnt >= 1
     * Invariant2: descendantCnt > 1 && resideComp == null ||  
     *             descendentCnt == 1 && resideComp != null
     */
    public Class<? extends K> compClass;
    public K residingComp;
    public int descendantCnt; // inclusive(includes descendants and itself)
    public ItemInfo<K> firstChild;
    public ItemInfo<K> parent;
    public ItemInfo<K> sibling;

    private ItemInfo(Class<? extends K> compClass, 
                     K residingComp, int descendantCnt, ItemInfo<K> firstChild,
                     ItemInfo<K> parent, ItemInfo<K> sibling) {
        this.compClass = compClass;
        this.residingComp = residingComp;
        this.descendantCnt = descendantCnt;
        this.firstChild = firstChild;
        this.parent = parent;
        this.sibling = sibling;
    }
    
    public static <K> ItemInfo<K> makeLeaf (Class<? extends K> compClass, K residingComp) {
        return new ItemInfo<K>(compClass, residingComp, 1, null, null, null);
    }


    public static <K> ItemInfo<K> makeInner (Class<? extends K> compClass, ItemInfo<K> ... children) {
        ItemInfo<K> result;
        result = new ItemInfo<K>(compClass, null, 1, null, null, null);
        for (ItemInfo<K> i : children) {
            result.addChild(i);
        }
        return result;
    }

    public boolean isLeaf() {
        return firstChild == null;
    }
    
    public boolean isRoot() {
        return parent == null;
    }
    
    public void addChild(ItemInfo<K> child) {
        child.parent = this;
        child.sibling = this.firstChild;
        this.firstChild = child;
        
        ItemInfo<K> iter = this;
        int addCnt = child.descendantCnt;
        while (iter != null) {
            iter.descendantCnt += addCnt;
            iter = iter.parent;
        }
    }
    
    public ItemInfo<K> remove() {
        // returns the node which was a parent of "this"
        // or null if this was the root
        ItemInfo<K> iter = this.parent;
        int rmCnt = descendantCnt;
        while (iter != null) {
            iter.descendantCnt -= rmCnt;
            iter = iter.parent;
        }

        if (parent != null) {
            iter = parent.firstChild;
            ItemInfo<K> prev = null;
            while (iter != this) {
                prev = iter;
                iter = iter.sibling;
            }
            if (iter != null) {
                if (prev != null)
                    prev.sibling = iter.sibling;
                else
                    parent.firstChild = iter.sibling;
            }
            return parent;
        } else
            return null;
    }
    
    private Iterable<ItemInfo<K>> ancestorsIterable = new Iterable<ItemInfo<K>>() {
            @Override
            public Iterator<ItemInfo<K>> iterator() {
                return new Iterator<ItemInfo<K>>() {
                    private ItemInfo<K> iter = parent;
                    @Override
                    public boolean hasNext() {
                        return iter != null;
                    }

                    @Override
                    public ItemInfo<K> next() {
                        ItemInfo<K> res = iter;
                        iter = iter.parent;
                        return res;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };

    public Iterable<ItemInfo<K>> ancestors() {
        return ancestorsIterable;
    }

    private Iterable<ItemInfo<K>> childrenIterable = new Iterable<ItemInfo<K>>() {
            @Override
            public Iterator<ItemInfo<K>> iterator() {
                return new Iterator<ItemInfo<K>>() {
                    private ItemInfo<K> iterSibling = firstChild;
                    @Override
                    public boolean hasNext() {
                        return iterSibling != null;
                    }

                    @Override
                    public ItemInfo<K> next() {
                        ItemInfo<K> res = iterSibling;
                        iterSibling = iterSibling.sibling;
                        return res;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };

    public Iterable<ItemInfo<K>> children() {
        return childrenIterable;
    }
    
    private Iterable<K> leafIterable = new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Stack<ItemInfo<K>> stack = new Stack<ItemInfo<K>>();

                    {
                        stack.add(ItemInfo.this);
                        readyStack();
                    }
                    
                    private void readyStack() {
                        // Invariant after this method: stack.peek().residingComp != null || 
                        //                              stack.isEmpty()

                        while (!stack.isEmpty() && stack.peek().residingComp == null) {

                            for (ItemInfo<K> i : stack.pop().children()) {
                                stack.push(i);
                            }
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return !stack.isEmpty();
                    }

                    @Override
                    public K next() {
                        K result = stack.peek().residingComp;
                        for (ItemInfo<K> i : stack.pop().children()) {
                            stack.push(i);
                        }
                        readyStack();
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                        
                    }
                };
            }
        };

    public Iterable<K> leafComps() {
        return leafIterable;
    }
    
    private Iterable<K> componentsIterable = new Iterable<K> () {
        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {
                boolean exhausted = false;
                K justOne;
                Iterator<K> iter;
                {
                    ItemInfo<K> info = ItemInfo.this;

                    if (info.isLeaf())
                        justOne = info.residingComp;
                    else {
                        justOne = null;
                        iter = info.leafComps().iterator();
                    }
                }
                @Override
                public boolean hasNext() {
                    return !exhausted && (justOne != null || justOne == null && iter.hasNext());
                }

                @Override
                public K next() {
                    K result;
                    if (justOne != null) {
                        result = justOne;
                        exhausted = true;
                    } else {
                        result = iter.next();
                    }
                    return result;
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    };
    
    public Iterable<K> components () {
        return componentsIterable;
    }
    

    @Override
    public Iterator<ItemInfo<K>> iterator() {
        return new Iterator<ItemInfo<K>>() {
            Stack<ItemInfo<K>> stack = new Stack<ItemInfo<K>>();
            {
                stack.push(ItemInfo.this);
            }

            @Override
            public boolean hasNext() {
                return !stack.empty();
            }

            @Override
            public ItemInfo<K> next() {
                ItemInfo<K> popee = stack.pop();
                for (ItemInfo<K> c : popee.children())
                    stack.add(c);
                return popee;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

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
            throw new TypeConflictError("Tried to remove component of " + clazz.toString() +
                                        ", but already have no component of the same or its derived class");
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


public class Item <K extends IAdherable<?>, OWNER> implements Iterable<K>, IAdherable<OWNER> {
    private ItemInfoContainer<K> container;
    private OWNER owner;
    private Class<K> kClass;
    
    public Item (Class<K> kClass, OWNER owner) {
        this.kClass = kClass;
        this.container = new ItemInfoContainer<K>(kClass);
        adhereTo(owner);
    }
    
    public Item (Class<K> kClass) {
        this.container = new ItemInfoContainer<K>(kClass);
        this.kClass = kClass;
        this.owner = null;
    }

    public <T extends K> T as(Class<T> typeClass) {
        Iterator<T> iter = this.many(typeClass).iterator();
        
        if (iter.hasNext()) {
            return iter.next();
        } else {
            throw new KeyError(typeClass.toString());
        }
    }
    
    public <T extends K> Iterable<T> many(Class<T> typeClass) {
        return this.container.filter(typeClass);
    }
    public <T extends K> T only(Class<T> typeClass) {
        Iterator<T> iter = this.many(typeClass).iterator();
        if (!iter.hasNext()) {
            throw new KeyError(typeClass.toString());
        }
        T result = iter.next();
        if (iter.hasNext()) {
            throw new TypeConflictError(typeClass.toString() + " is duplicated");
        }
        return result;
    }
    
    public boolean has(Class<? extends K> typeClass) {
        return this.container.has(typeClass);
    }
    

    @SuppressWarnings("unchecked")
    public boolean add(K elem) {
        this.container.add(elem);
        ((IAdherable<Object>)elem).adhereTo(this);
        return true;
    }
    
    public Iterable<K> remove (Class<? extends K> key) {
        Iterable<K> rem = this.container.remove(key);
        for (K comp : rem) {
            comp.adhereTo(null);
        }
        return rem;
    }
    
    public void removeAll () {
        remove (kClass);
    }

    public Iterator<K> iterator() {
        return this.container.iterator();
    }
    
    public static Item<BaseComp, ICore> MakeCompItem() {
        return new Item<BaseComp, ICore>(BaseComp.class);
    }
    
    public static Item<BaseCompServer, ICore> MakeServerItem() {
        return new Item<BaseCompServer, ICore>(BaseCompServer.class);
    }


    @Override
    public OWNER owner() {
        return owner;
    }

    @Override
    public void adhereTo(OWNER c) {
        this.owner = c; 
    }
}