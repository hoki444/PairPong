package com.algy.schedcore.util;

public class Pair <A, B> {
    public A first;
    public B second;
    
    public Pair (A first, B second) {
        this.first = first;
        this.second = second;
    }
    
    public static <A, B> Pair<A, B> cons(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.first == null) ? 0 : this.first.hashCode());
        result = prime * result
                + ((this.second == null) ? 0 : this.second.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (this.first == null) {
            if (other.first != null)
                return false;
        } else if (!this.first.equals(other.first))
            return false;
        if (this.second == null) {
            if (other.second != null)
                return false;
        } else if (!this.second.equals(other.second))
            return false;
        return true;
    }

}
