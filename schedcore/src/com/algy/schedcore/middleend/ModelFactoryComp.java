package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.AssetUsable;
import com.algy.schedcore.middleend.asset.ModelFactory;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;

public class ModelFactoryComp extends Renderable3DComp implements AssetUsable {
    public Model model = null;
    public ModelFactory mf;
    public Matrix4 localTransform;
    
    public ModelFactoryComp (ModelFactory mf) {
        this (mf, new Matrix4());
    }

    public ModelFactoryComp (ModelFactory mf, Matrix4 localTransform) {
        this.mf = mf;
        this.localTransform = localTransform;
    }

    @Override
    public IComp duplicate() {
        return new ModelFactoryComp(mf, localTransform);
    }

    @Override
    public RenderableProvider renderableProvider() {
        return new ModelInstance(model, localTransform);
    }


    @Override
    public void declareAsset(AssetList assetListOut) {
        mf.declareAsset(assetListOut);
    }

    @Override
    public void onItemAdded() {
        model = mf.make(server(AssetServer.class));
    }

    @Override
    protected void onDetached() {
        model.dispose();
    }
}
