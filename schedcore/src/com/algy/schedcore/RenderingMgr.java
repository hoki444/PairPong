package com.algy.schedcore;

import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

interface RenderingModule extends Disposable {
    public void create ();
    public void begin ();
    public void render (Iterable<BaseComp> components);
    public void end ();
    public void hook (Lister<Class<? extends BaseComp>> sigList);

    @Override
    public void dispose();
}

public class RenderingMgr {
    void f () {
        ModelBatch s;
        ShaderProgram t = null;
        Shader sd;
        Renderable r;
        t.begin();
        t.end();
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}