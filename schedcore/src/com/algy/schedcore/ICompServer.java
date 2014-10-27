package com.algy.schedcore;

import java.util.List;


public interface ICompServer extends IAdherable<Item<BaseCompServer, ICore>> {
    public List<Class<? extends BaseComp>> hookFilters();
	public void hookAddComp(BaseComp comp);
	public void hookRemoveComp(BaseComp comp);
}