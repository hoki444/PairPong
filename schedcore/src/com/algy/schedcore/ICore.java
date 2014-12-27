package com.algy.schedcore;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.Item;
import com.algy.schedcore.Scheduler;

public interface ICore {
	public void addItem (Item<BaseComp, ICore> item);
	public void removeItem (Item<BaseComp, ICore> item);

	public void addComp (Item<BaseComp, ICore> owner, BaseComp comp);
	public void removeComp (BaseComp comp);
	public <T extends BaseCompServer> T server(Class<T> cls);
	
	public <T extends BaseCompServer> boolean addServer(T server);
    public Iterable<BaseCompServer> removeServer(Class<? extends BaseCompServer> serverClass);
	public Scheduler scheduler();
}