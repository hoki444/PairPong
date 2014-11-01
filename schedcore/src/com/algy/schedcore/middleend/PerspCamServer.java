package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.util.MutableLister;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class PerspCamServer extends CameraServer {
    private PerspectiveCamera camera;
    public PerspCamServer (float vwidth, float vheight, float fov) {
        camera = new PerspectiveCamera(fov, vwidth, vheight);
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
