package com.algy.schedcore;

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