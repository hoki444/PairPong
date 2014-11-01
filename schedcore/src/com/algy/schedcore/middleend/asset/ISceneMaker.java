package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.middleend.GameItem;

public interface ISceneMaker {
    public static enum OpType {
        New, Remove, Alter
    }

    public static class Operation {
        public OpType type;
        public GameItem item;
        public IItemModifier alterer = null;
    }

    public Iterable<Operation> incrementalGet(Eden eden);
    public Iterable<GameItem> bulkGet(Eden eden);
}