package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.algy.schedcore.util.ResourceBox;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;

public class ModelComp extends Renderable3DComp {
    // This component borrow ownership of the model.
    // Thus, outer environment is responsible for disposing the model
    public ResourceBox<Model> modelBox;
    public boolean refCounted;
    public Matrix4 localTransform;
    public ModelComp (Model model, Matrix4 localTransform) {
        this.modelBox = ResourceBox.modelBox(model).obtain();
        this.refCounted = false;
        this.localTransform = localTransform;
    }
    
    public ModelComp (Model model) {
        this(model, new Matrix4());
    }

    public ModelComp (ResourceBox<Model> modelBox, Matrix4 localTransform) {
        this.modelBox = modelBox.obtain();
        this.refCounted = true;
        this.localTransform = localTransform;
    }

    public ModelComp (ResourceBox<Model> modelBox) {
        this(modelBox, new Matrix4());
    }

    @Override
    public IComp duplicate() {
        return new ModelComp(modelBox, localTransform);
    }

    @Override
    public RenderableProvider renderableProvider() {
        return new ModelInstance(modelBox.get(), localTransform);
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
        if (refCounted)
            modelBox.release();
    }

}