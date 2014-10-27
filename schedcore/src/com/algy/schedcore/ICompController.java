package com.algy.schedcore;

public interface ICompController {
    public void addItem(Item<BaseComp, ICore> item);
    public void removeItem(Item<BaseComp, ICore> item);
    public void addComp(Item<BaseComp, ICore> owner, BaseComp comp);
    public void removeComp(BaseComp comp);
    
    public <T extends BaseCompServer> T server(Class<T> cls); // may throw TypeConflictError
}