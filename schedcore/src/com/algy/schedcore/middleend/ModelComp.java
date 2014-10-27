package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;

public class ModelComp extends RenderComp {
    // This component borrow ownership of the model.
    // Thus, outter environment is responsible for disposing the model
    public ModelInstance modelInstance;
    public ModelComp (ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    @Override
    public IComp duplicate() {
        return new ModelComp(modelInstance);
    }

    @Override
    public RenderableProvider renderableProvider() {
        return modelInstance;
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }

}
