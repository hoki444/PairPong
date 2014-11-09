package com.algy.schedcore;

import java.util.Iterator;

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