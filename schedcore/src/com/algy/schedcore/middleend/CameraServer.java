package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseCompServer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public abstract class CameraServer extends BaseCompServer {
    private boolean isDirty = false;
    
    protected abstract Camera getInternalCamera ();

    // Aspect from outter env
    public Camera getCamera() {
        Camera cam = getInternalCamera();
        if (isDirty) {
            cam.update();
            isDirty = false;
        }
        return cam;
    }
    
    protected void setDirty() { 
        this.isDirty = true;
    }

    public void setUpVector (Vector3 vec) {
        setDirty();
        getInternalCamera().up.set(vec);
    }

    public void setPosition (Vector3 vec) {
        setDirty();
        getInternalCamera().position.set(vec);
    }
    
    public void setRange (float near, float far) {
        setDirty();
        getInternalCamera().near = near;
        getInternalCamera().far = far;
    }
    
    public void lookAt (Vector3 vec) {
        setDirty();
        this.getInternalCamera().lookAt(vec);
    }

}