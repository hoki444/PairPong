package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;

public class ModelComp extends Renderable3DComp {
    // This component borrow ownership of the model.
    // Thus, outter environment is responsible for disposing the model
    public Model model;
    public Matrix4 localTransform;
    public ModelComp (Model model, Matrix4 localTransform) {
        this.model = model;
        this.localTransform = localTransform;
    }

    public ModelComp (Model model) {
        this(model, new Matrix4());
    }

    @Override
    public IComp duplicate() {
        return new ModelComp(model);
    }

    @Override
    public RenderableProvider renderableProvider() {
        return new ModelInstance(model, localTransform);
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }

}