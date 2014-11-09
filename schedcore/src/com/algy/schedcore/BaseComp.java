package com.algy.schedcore;

public abstract class BaseComp implements IComp {
    private Item<BaseComp, ICore> owner;

    public Item<BaseComp, ICore> owner() {
        return this.owner;
    }
    public Item<BaseComp, ICore> item () {
        // just an alias of "owner()"
        return this.owner;
    }
    
    public ICore core() {
        return owner.owner();
    }
    
    public final <T extends BaseComp> T other (Class<T> clazz) {
        return owner().as(clazz);
    }
    
    public <T extends BaseCompServer> T server(Class<T> cls) {
        return this.owner().owner().server(cls);
    }

    public final void adhereTo(Item<BaseComp, ICore> c) {
        this.owner = c;
        if (c != null) {
            onAdhered();
        } else {
            onDetached();
        }
    }
    
    public void onItemAdded () { }
    public void onItemRemoved () { }
    protected void onAdhered() { }
    protected void onDetached() { }
}