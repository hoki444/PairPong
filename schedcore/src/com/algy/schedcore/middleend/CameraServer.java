package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraServer extends BaseCompServer {
    private boolean isDirty = false;
    private boolean isPersp;
    private OrthographicCamera ortho;
    private PerspectiveCamera persp;
    
    public CameraServer (float vwidth, float vheight, float fieldOfView) {
        isPersp = true;
        persp = new PerspectiveCamera(fieldOfView, vwidth, vheight);
    }

    public CameraServer (float vwidth, float vheight) {
        isPersp = false;
        ortho = new OrthographicCamera(vwidth, vheight);
    }
    
    protected Camera getInternalCamera () {
        return isPersp? persp : ortho;
    }

    // Aspect from outter env
    public Camera getCamera() {
        Camera cam = getInternalCamera();
        if (isDirty) {
            cam.update();
            isDirty = false;
        }
        return cam;
    }
    
    public void setCamera (Camera camera) {
        if (camera instanceof PerspectiveCamera) {
            isDirty = false;
            isPersp = true;
            persp = (PerspectiveCamera)camera;
        } else if (camera instanceof OrthographicCamera) {
            isDirty = false;
            isPersp = false;
            ortho = (OrthographicCamera)camera;
        } else
            throw new IllegalArgumentException();
    }
    
    public boolean isPerspective () { 
        return isPersp;
    }
    
    public void setFieldOfView (float fieldOfView) {
        if (!isPersp)
            throw new IllegalStateException("Camera server has an orthographic camera");
        persp.fieldOfView = fieldOfView;
        setDirty();
    }
    
    public void resizeViewPort (float width, float height) {
        getInternalCamera().viewportWidth = width;
        getInternalCamera().viewportHeight = height;
        setDirty();
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
    public float getNear () {
        return getInternalCamera().near;
    }
    
    public float getFar () {
        return getInternalCamera().far;
    }

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList) { }

    @Override
    public void hookAddComp(BaseComp comp) { }

    @Override
    public void hookRemoveComp(BaseComp comp) { }

    @Override
    protected void onAdhered() { }

    @Override
    protected void onDetached() { }

}