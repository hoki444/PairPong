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

    public CameraServer setUpVector (Vector3 vec) {
        setDirty();
        getInternalCamera().up.set(vec);
        return this;
    }

    public CameraServer setPosition (Vector3 vec) {
        setDirty();
        getInternalCamera().position.set(vec);
        
        return this;
    }
    
    public Vector3 getPosition () {
        return getInternalCamera().position;
    }
    
    public Vector3 getUpVector () {
        return getInternalCamera().up;
    }
    
    public CameraServer setRange (float near, float far) {
        setDirty();
        getInternalCamera().near = near;
        getInternalCamera().far = far;
        return this;
    }
    
    public CameraServer lookAt (Vector3 vec) {
        setDirty();
        this.getInternalCamera().lookAt(vec);
        return this;
    }

}