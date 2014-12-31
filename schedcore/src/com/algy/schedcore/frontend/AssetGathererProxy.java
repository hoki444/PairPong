package com.algy.schedcore.frontend;

import com.algy.schedcore.GameItem;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetSig;

public interface AssetGathererProxy {
    public void gatherFromGameItem (GameItem gameItem);
    public void gatherFromGameItem (Iterable<GameItem> gameItems);
    public void gatherFromPrototype (String prototypeName);
    public void gatherFromPrototype (String ... prototypeNames);
    public void gather (AssetList assetList);
    public void gather (AssetSig assetSig);
    public void gather (String fileName, Class<?> type);
}