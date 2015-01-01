package com.algy.schedcore;

public interface Attachable<Container> {
    public Container owner();

    // This function mutates the future result of selector owner().
    // If c is null, it detaches its own container.
    public void attachTo(Container c); 
}