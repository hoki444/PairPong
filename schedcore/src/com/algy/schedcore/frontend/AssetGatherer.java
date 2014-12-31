package com.algy.schedcore.frontend;

import com.algy.schedcore.GameItem;
import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetSig;

public class AssetGatherer implements AssetGathererProxy {
    private Eden eden;

    public AssetList assetList = new AssetList();
    
    public AssetGatherer (Eden eden) {
        this.eden = eden;
    }

    @Override
    public void gatherFromGameItem(GameItem gameItem) {
        gameItem.getUsedAsset(assetList);
    }

    @Override
    public void gatherFromGameItem(Iterable<GameItem> gameItems) {
        for (GameItem gameItem : gameItems) {
            gameItem.getUsedAsset(assetList);
        }
    }

    @Override
    public void gatherFromPrototype(String prototypeName) {
        eden.getUsedAsset(prototypeName, assetList);
    }

    @Override
    public void gatherFromPrototype(String... prototypeNames) {
        for (String prototypeName : prototypeNames) {
            eden.getUsedAsset(prototypeName, assetList);
        }

    }

    @Override
    public void gather(AssetList assetList) {
        this.assetList.update(assetList);
    }

    @Override
    public void gather(AssetSig assetSig) {
        this.assetList.add(assetSig);
    }

    @Override
    public void gather(String fileName, Class<?> type) {
        this.assetList.add(fileName, type);
    }

    public AssetList getGatheredAssetList () {
        return assetList;
    }
}
