package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.BaseComp;

public interface IItemModifier {
    public boolean modifiable (Class<? extends BaseComp> compClass);
    public ICompModifier compModifier (Class<? extends BaseComp> compClass);
}
