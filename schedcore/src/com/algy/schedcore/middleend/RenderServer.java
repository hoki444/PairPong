package com.algy.schedcore.middleend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;

public class RenderServer extends BaseCompServer {
    private HashSet<RenderComp> renderComps = new HashSet<RenderComp>();
    @Override
    public List<Class<? extends BaseComp>> hookFilters() {
        ArrayList<Class<? extends BaseComp>> sigs = new ArrayList<Class<? extends BaseComp>>();
        sigs.add(RenderComp.class);
        return sigs;
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        renderComps.add((RenderComp)comp);
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        renderComps.remove((RenderComp)comp);
    }
    
    public void render (ModelBatch batch, Environment env, Shader shader) {
        for (RenderComp comp : renderComps) 
            batch.render(comp, env, shader);
    }

    public void render (ModelBatch batch, Environment env) {
        for (RenderComp comp : renderComps) 
            batch.render(comp, env);
    }

    public void render (ModelBatch batch) {
        for (RenderComp comp : renderComps) 
            batch.render(comp);
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }
}
