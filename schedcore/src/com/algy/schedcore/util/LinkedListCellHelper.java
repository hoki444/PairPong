package com.algy.schedcore.util;

public class LinkedListCellHelper {
    public static LinkedListCell simpleCell ( ) {
        return new LinkedListCell() {
            LinkedListCell prev = null, next = null;
            @Override
            public void setPrev(LinkedListCell cell) {
                this.prev = cell;
            }
            
            @Override
            public void setNext(LinkedListCell cell) {
                this.next = cell;
            }
            
            @Override
            public LinkedListCell getPrev() {
                return prev;
            }
            
            @Override
            public LinkedListCell getNext() {
                return next;
            }
        };
    }
    
    public static void pushNext(LinkedListCell dest, LinkedListCell src) {
        src.setNext(dest.getNext());
        src.setPrev(dest);

        LinkedListCell dest_next = dest.getNext();
        if (dest_next != null)
            dest_next.setPrev(src);
        dest.setNext(src);
    }
    
    public static LinkedListCell popDest (LinkedListCell dest) {
        LinkedListCell dest_next = dest.getNext(), dest_prev = dest.getPrev();
        if (dest_next != null)
            dest_next.setPrev(dest_prev);
        if (dest_prev != null)
            dest_prev.setNext(dest_next);
        dest.setNext(null);
        dest.setPrev(null);
        return dest_prev;
    }
}
