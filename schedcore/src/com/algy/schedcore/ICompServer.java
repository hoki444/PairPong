package com.algy.schedcore;

import java.util.List;

import com.algy.schedcore.util.MutableLister;


public interface ICompServer extends IAdherable<Item<BaseCompServer, ICore>> {
    public void hookFilters(MutableLister<Class<? extends BaseComp>> compSigList);
	public void hookAddComp(BaseComp comp);
	public void hookRemoveComp(BaseComp comp);
}