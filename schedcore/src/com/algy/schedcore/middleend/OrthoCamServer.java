package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.util.MutableLister;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class OrthoCamServer extends CameraServer {
    private OrthographicCamera camera;

    public OrthoCamServer(float vwidth, float vheight) {
        this.camera = new OrthographicCamera(vwidth, vheight);
    }

    @Override
    public void hookFilters(MutableLister<Class<? extends BaseComp>> sigs) {
    }

    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }

    @Override
    protected Camera getInternalCamera() {
        return camera;
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }

}