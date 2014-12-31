package com.algy.schedcore;

import com.algy.schedcore.util.Lister;



public abstract class BaseCompMgr implements Attachable<CompMgrSpace> {
    private CompMgrSpace owner;

    @Override
    public final CompMgrSpace owner() {
        return owner;
    }

    @Override
    public final void attachTo(CompMgrSpace c) {
        this.owner = c;
        if (c != null)
            onAttached();
        else
            onDetached();
    }
    public abstract void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList);
    public abstract void hookAddComp(BaseComp comp);
    public abstract void hookRemoveComp(BaseComp comp);


    protected void onAttached() {}
    protected void onDetached() {}
}