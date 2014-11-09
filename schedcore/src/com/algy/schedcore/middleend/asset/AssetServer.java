package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.Lister;

public class AssetServer extends BaseCompServer implements AssetProvider {
    public LazyAssetManager assetManager = null;
    
    public AssetServer (LazyAssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList) {
    }

    public boolean isOnTheFlyLoadingEnabled() {
        return this.assetManager.isOnTheFlyLoadingEnabled();
    }

    public void enableOnTheFlyLoading(boolean enable) {
        this.assetManager.enableOnTheFlyLoading(enable);
    }

    public <T> T loadImmediately(String fileName, Class<T> type) {
        return this.assetManager.loadImmediately(fileName, type);
    }

    @Override
    public <T> T get(String fileName, Class<T> type) {
        return this.assetManager.get(fileName, type);
    }
}