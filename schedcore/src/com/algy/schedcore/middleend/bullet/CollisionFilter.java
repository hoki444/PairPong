package com.algy.schedcore.middleend.bullet;

public class CollisionFilter {
    public final short group;
    public final short mask;
    
    public CollisionFilter (short group, short mask) {
        this.group = group;
        this.mask = mask;
    }

    public CollisionFilter duplicate() {
        return new CollisionFilter(group, mask);
    }
}
