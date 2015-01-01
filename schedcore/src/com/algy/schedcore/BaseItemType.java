package com.algy.schedcore;

public abstract class BaseItemType implements Attachable<GameItem> {
    private GameItem owner;
    @Override
    public final GameItem owner() {
        return owner;
    }
    
    public final GameItem getGameItem () {
        return owner;
    }

    @Override
    public final void attachTo(GameItem c) {
        owner = c;
    }
    
    public abstract BaseItemType duplicate();
    
    public boolean isTypeOf(Class<? extends BaseItemType> clazz) {
        return this.getClass().isAssignableFrom(clazz);
    }
    
    // Aspect from internal closure
    protected <T extends BaseComp> T getComp (Class<T> clazz) {
        return owner.as(clazz);
    }
}