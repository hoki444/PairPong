package com.algy.schedcore.frontend;

import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetLoadingController;

public interface SceneResourceInitializer {
    public void beginResourceInitialization (Scene scene); 
    public void reserveItem (Scene scene, ItemReservable reservable);
    public void gatherAsset (Scene scene, AssetGathererProxy gatherer);
    public void loadAsset (Scene scene, AssetLoadingController controller, AssetList newAssetList);
    public void endResourceInitialization (Scene scene); 
}