package com.algy.schedcore;

public abstract class BaseComp implements Attachable<GameItem> {
    private GameItem owner;

    
    public GameItem owner() {

        return this.owner;
    }

    public GameItem item () {
        // just an alias of "owner()"
        return this.owner;
    }
    public abstract BaseComp duplicate();
    
    public GameItemSpace core() {
        return owner.owner();
    }
    
    public final <T extends BaseComp> T other (Class<T> clazz) {
        return owner().as(clazz);
    }
    
    public <T extends BaseCompMgr> T getCompManager(Class<T> cls) {
        return this.owner().owner().getCompMgrSpace().as(cls);
    }

    @Override
    public final void attachTo(GameItem c) {
        this.owner = c;
        if (c != null) {
            onAttached();
        } else {
            onDetached();
        }
    }
    
    public void onItemAdded () { }
    public void onItemRemoved () { }
    protected void onAttached() {}
    protected void onDetached() {}

}