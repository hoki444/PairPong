package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.MutableLister;
import com.badlogic.gdx.assets.AssetManager;

public class AssetManagerServer extends BaseCompServer {
    /*
     * 
     */
    public AssetManager asset = null;
    
    public AssetManagerServer (AssetManager assetManager) {
        this.asset = assetManager;
    }
    
    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }

    @Override
    public void hookFilters(MutableLister<Class<? extends BaseComp>> compSigList) {
    }
}