package com.algy.schedcore;

class HeapNode {
    public JobInfo content;
    public HeapNode left = null, right = null, parent = null, linePrev = null, lineNext = null;
    
    public HeapNode (JobInfo content) {
        this.content = content;
    }
    
    public void clear() {
        left = null; 
        right = null; 
        parent = null; 
        linePrev = null;
        lineNext = null;
    }
}

class BinheapQueue {
    public int size = 0;
    public HeapNode root = null;
    public HeapNode rightmostLeaf = null;
    public boolean isPendingQueue;
    
    public BinheapQueue (boolean isPendingQueue) {
        this.isPendingQueue = isPendingQueue;
    }

    public synchronized boolean isEmpty () {
        return root == null;
    }

    public synchronized int size () {
        return size;
    }
 
    public synchronized JobInfo peek () {
        return root.content;
    }
    
    public synchronized JobInfo shallowPop () {
        if (root != null) {
            JobInfo ji = root.content;
            JobInfo ji_next;
            if ((ji_next = ji.popNext()) != null) {
                return ji_next;
            } else {
                return deepPop();
            }
        } else {
            return null;
        }
    }

    public synchronized JobInfo deepPop () {
        return remove(root.content);
    }

    public synchronized void push (JobInfo ji) {
        HeapNode heapNode = ji.heapNode;
        heapNode.clear();

        if (size == 0) {
            root = rightmostLeaf = heapNode;
            size++;
        } else { 
            HeapNode anchor = null;
            boolean appendToLeft = true;
            boolean newLine = false;

            if (rightmostLeaf.parent == null) {
                newLine = true;
                anchor = rightmostLeaf;
            } else if (rightmostLeaf.parent.left == rightmostLeaf) {
                appendToLeft = false;
                anchor = rightmostLeaf.parent;
            } else if (rightmostLeaf.parent.lineNext == null) {
                newLine = true;
                HeapNode p = root;
                while (p.left != null) {
                    p = p.left;
                }
                anchor = p;
            } else {
                anchor = rightmostLeaf.parent.lineNext;
            }

            if (appendToLeft) {
                // append to left
                anchor.left = heapNode;
            } else {
                // append to right
                anchor.right = heapNode;
            }
            heapNode.parent = anchor;

            if (newLine) {
                rightmostLeaf.lineNext = null;
                heapNode.linePrev = null; 
            } else {
                rightmostLeaf.lineNext = heapNode;
                heapNode.linePrev = rightmostLeaf;
            }
            rightmostLeaf = heapNode;
            size++;
            upheap(heapNode);
        }
    }

    public synchronized JobInfo remove(JobInfo ji) {
        HeapNode heapNode = ji.heapNode;
        if (size == 0) {
            return null;
        } 

        if (rightmostLeaf == root) {
            if (root == heapNode) {
                JobInfo result = root.content;
                root.clear();
                root = rightmostLeaf = null;
                size--;
                return result;
            } else
                return null;
        } 

        swap(heapNode, rightmostLeaf);
        if (rightmostLeaf.parent != null) {
            if (rightmostLeaf.parent.left == rightmostLeaf)
                rightmostLeaf.parent.left = null;
            else
                rightmostLeaf.parent.right = null;
        }

        HeapNode newRightmost;
        if (rightmostLeaf.linePrev != null) {
            rightmostLeaf.linePrev.lineNext = null;
            newRightmost = rightmostLeaf.linePrev;
        } else {
            HeapNode p = root;
            while (p.right != null)
                p = p.right;
            newRightmost = p;
        }
        JobInfo result = rightmostLeaf.content;
        rightmostLeaf.clear();
        rightmostLeaf = newRightmost;
        size--;

        if (newRightmost == null)
            root = null;
        else if (heapNode.parent != null && cmp(heapNode.parent, heapNode) > 0)
            upheap(heapNode);
        else
            downheap(heapNode);
        return result;
    }
    
    private void upheap(HeapNode node) {
        HeapNode deadEnd = node.parent;
        boolean mergable = false;
        while (deadEnd != null) {
            int test = cmp(node, deadEnd);
            if (test > 0) {
                break;
            } else if (test == 0) {
                mergable = true;
                break;
            }
            deadEnd = deadEnd.parent;
        }

        if (mergable) {
            JobInfo ji = node.content;
            JobInfo deadEndJi = deadEnd.content;
            deadEndJi.pushNext(ji);
            remove(ji);
        } else {
            while (node.parent != deadEnd) {
                swap(node, node.parent);
                node = node.parent;
            }
        }

    }

    private void downheap(HeapNode node) {
        while (node.left != null) {
            HeapNode cand = node;
            if (cmp(cand, node.left) > 0)
                cand = node.left;
            if (node.right != null && cmp(cand, node.right) > 0)
                cand = node.right;
            if (node != cand) {
                swap(node, cand);
                node = cand;
            } else
                break;
        }
    }

    private void swap(HeapNode lhs, HeapNode rhs) {
        lhs.content.heapNode = rhs;
        rhs.content.heapNode = lhs;

        JobInfo tmp = lhs.content;
        lhs.content = rhs.content;
        rhs.content = tmp;
    }
    
    private int cmp (HeapNode lhs, HeapNode rhs) {
        long a, b;
        if (isPendingQueue) { // EDF
            a = lhs.content.curDeadline; 
            b = rhs.content.curDeadline;
        } else { // Early FutureReltime First
            a = lhs.content.futureReltime;
            b = rhs.content.futureReltime;
        }

        if (a < b)
            return -1;
        else if (a > b)
            return 1;
        else
            return 0;
    }
    
       
    public int dbgcount () {
        Assert(_inv(root));
        return _count(root);
    }
    
    private boolean _inv (HeapNode heapNode) {
        if (heapNode == null)
            return true;
        else if (_inv(heapNode.left) && _inv(heapNode.right)) {
            return heapNode.parent == null || cmp(heapNode.parent, heapNode) <= 0;
        } else
            return false;
    }
    
    private static int _count (HeapNode heapNode) {
        int result = 0;
        boolean hasChild = false;
        
        if (heapNode == null)
            return 0;
        
        if (heapNode.left != null) {
            result += _count(heapNode.left);
            Assert(heapNode.left.parent == heapNode);
            hasChild = true;
                    
        }
        if (heapNode.right != null) {
            result += _count(heapNode.right);
            Assert(heapNode.right.parent == heapNode);
            hasChild = true;
        }
        
        if (heapNode.lineNext != null) {
            Assert (heapNode.lineNext.linePrev == heapNode);
        }
        if (heapNode.linePrev != null) {
            Assert (heapNode.linePrev.lineNext == heapNode);
        }
        
        for (JobInfo ji = heapNode.content; ji != null; ji = ji.next) {
            if (heapNode.content == ji)
                Assert (ji.prev == null);
            
            if (ji.prev != null) {
                Assert(ji.prev.next == ji);
            }
            if (ji.next != null) {
                Assert(ji.next.prev == ji);
            }
            result++;
        }
        return result;
    }
    private static void Assert (boolean test) {
        if (!test) {
            throw new RuntimeException("Invariant BROKEN!");
        }
    }
}