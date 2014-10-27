package com.algy.schedcore;

public interface ICore extends ICompController {
	public void addItem (Item<BaseComp, ICore> item);
	public void removeItem (Item<BaseComp, ICore> item);

	public void addComp (Item<BaseComp, ICore> owner, BaseComp comp);
	public void removeComp (BaseComp comp);
	public <T extends BaseCompServer> T server(Class<T> cls);
	
	public <T extends BaseCompServer> boolean addServer(T server);
    public Iterable<BaseCompServer> removeServer(Class<? extends BaseCompServer> serverClass);
	public Scheduler sched();
}