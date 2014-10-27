package com.algy.schedcore;

public interface IComp extends IAdherable<Item<BaseComp, ICore>> {
    IComp duplicate();
}