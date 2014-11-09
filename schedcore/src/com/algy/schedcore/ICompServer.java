package com.algy.schedcore;

import com.algy.schedcore.util.Lister;


public interface ICompServer extends IAdherable<Item<BaseCompServer, ICore>> {
    public void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList);
	public void hookAddComp(BaseComp comp);
	public void hookRemoveComp(BaseComp comp);
}