package com.algy.schedcore.middleend;

import java.util.HashSet;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class Render3DServer extends BaseCompServer {
    private HashSet<Renderable3DComp> renderableComps = new HashSet<Renderable3DComp>();
    private HashSet<ModelBatch3DComp> mbComps = new HashSet<ModelBatch3DComp>();

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> sigs) {
        sigs.add(Renderable3DComp.class);
        sigs.add(ModelBatch3DComp.class);
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        if (comp instanceof Renderable3DComp) {
            renderableComps.add((Renderable3DComp)comp);
        } else if (comp instanceof ModelBatch3DComp) {
            mbComps.add((ModelBatch3DComp) comp);
        }
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        if (comp instanceof Renderable3DComp) {
            renderableComps.remove(comp);
        } else if (comp instanceof ModelBatch3DComp) {
            mbComps.remove(comp);
        }
    }
    
    public void render (ModelBatch batch, Environment env) {
        for (Renderable3DComp comp : renderableComps) {
            batch.render(comp, env);
        }

        for (ModelBatch3DComp comp : mbComps) {
            comp.render(batch, env);
        }
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }
}
