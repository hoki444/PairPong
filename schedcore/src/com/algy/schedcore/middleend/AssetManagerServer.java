package com.algy.schedcore.middleend;

import java.util.List;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.badlogic.gdx.assets.AssetManager;

public class AssetManagerServer extends BaseCompServer {
    public AssetManager asset = null;
    
    @Override
    public List<Class<? extends BaseComp>> hookFilters() {
        return null;
    }

    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }

    @Override
    protected void onAdhered() {
        this.asset = new AssetManager();
    }

    @Override
    protected void onDetached() {
        this.asset.dispose();
    }
}