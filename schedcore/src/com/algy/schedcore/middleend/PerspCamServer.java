package com.algy.schedcore.middleend;

import java.util.List;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class PerspCamServer extends CameraServer {
    private PerspectiveCamera camera;
    public PerspCamServer (float vwidth, float vheight, float fov) {
        camera = new PerspectiveCamera(fov, vwidth, vheight);
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
