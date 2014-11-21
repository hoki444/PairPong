package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.AssetUsable;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;

public class AssetModelComp extends Renderable3DComp implements AssetUsable {
    public String assetName;
    public Matrix4 localTransform;
    
    public AssetModelComp (String assetName) {
        this (assetName, new Matrix4());
    }

    public AssetModelComp (String assetName, Matrix4 localTransform) {
        this.assetName = assetName;
        this.localTransform = localTransform;
    }

    @Override
    public IComp duplicate() {
        return new AssetModelComp(assetName, localTransform);
    }

    @Override
    public RenderableProvider renderableProvider() {
        return new ModelInstance(core().server(AssetServer.class).get(assetName, Model.class), 
                                 localTransform);
    }

    @Override
    public void declareAsset(AssetList assetListOut) {
        assetListOut.add(assetName, Model.class);
    }
}