package com.algy.schedcore.middleend;

import java.util.HashSet;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;

public class Render3DServer extends BaseCompServer {
    private HashSet<Renderable3DComp> renderComps = new HashSet<Renderable3DComp>();

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> sigs) {
        sigs.add(Renderable3DComp.class);
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        renderComps.add((Renderable3DComp)comp);
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        renderComps.remove((Renderable3DComp)comp);
    }
    
    public void render (ModelBatch batch, Environment env, Shader shader) {
        for (Renderable3DComp comp : renderComps) 
            batch.render(comp, env, shader);
    }

    public void render (ModelBatch batch, Environment env) {
        for (Renderable3DComp comp : renderComps) 
            batch.render(comp, env);
    }

    public void render (ModelBatch batch) {
        for (Renderable3DComp comp : renderComps) 
            batch.render(comp);
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }
}
