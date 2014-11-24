package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public abstract class ModelBatch3DComp extends BaseComp {
    public abstract void render (ModelBatch modelBatch, Environment defaultEnv);
}