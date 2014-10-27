package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public abstract class RenderComp extends BaseComp implements RenderableProvider {
    /*
     * Dependent on Transform
     */

    @Override
    public void getRenderables(Array<Renderable> arr, Pool<Renderable> pool) {
        int origLen = arr.size;
        renderableProvider().getRenderables(arr, pool);
        int newLen = arr.size;
        
        Transform transform = other(Transform.class);
        for (int idx = origLen; idx < newLen; idx++) {
            Renderable r = arr.get(idx);
            r.worldTransform.set(transform.get());
        }
    }
    public abstract RenderableProvider renderableProvider ();
}