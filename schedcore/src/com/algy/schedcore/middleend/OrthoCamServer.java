package com.algy.schedcore.middleend;

import java.util.List;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class OrthoCamServer extends CameraServer {
    private OrthographicCamera camera;

    public OrthoCamServer(float vwidth, float vheight) {
        this.camera = new OrthographicCamera(vwidth, vheight);
    }

    @Override
    public List<Class<? extends BaseComp>> hookFilters() {
        return null;
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
