package com.algy.schedcore;


public abstract class BaseCompServer implements ICompServer {
    private Item<BaseCompServer, ICore> owner;

    public final Item<BaseCompServer, ICore> owner() {
        return owner;
    }

    public final ICore core() {
        return owner.owner();
    }

    public final void adhereTo(Item<BaseCompServer, ICore> c) {
        this.owner = c;
        if (c != null)
            onAdhered();
        else
            onDetached();
    }

    protected abstract void onAdhered();
    protected abstract void onDetached();
}