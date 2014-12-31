package com.algy.schedcore.util;

public interface LinkedListCell {
    public LinkedListCell getNext();
    public LinkedListCell getPrev();
    public void setPrev(LinkedListCell cell);
    public void setNext(LinkedListCell cell);
}
