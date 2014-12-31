package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompServerTemplate;
import com.algy.schedcore.frontend.idl.reflection.SelectiveGroup;
import com.algy.schedcore.middleend.CameraServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;



public class CameraServerTemplate extends IDLCompServerTemplate {
    private static class Orthographic {}
    private static class Perspective { public Float fieldOfView = null; }
    static SelectiveGroup reflCamera = new SelectiveGroup("perspective", "orthographic");

    public Vector3 position = null;
    public Vector3 lookAt = null;
    public Vector3 up = null;
    public Float near = null;
    public Float far = null;

    public Perspective perspective = null;
    public Orthographic orthographic = null;

    @Override
    protected BaseCompMgr create(IDLGameContext context) {
        if (position == null) {
            position = new Vector3(0, 0, 1);
        }
        if (lookAt == null) {
            lookAt = new Vector3(0, 0, 0);
        }
        if (up == null) {
            up = new Vector3(0, 1, 0);
        }
        if (near == null)
            near = 1.f;
        if (far == null)
            far = 30.f;
        
        CameraServer result;
        if (orthographic != null) {
            result = new CameraServer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            float fov = perspective.fieldOfView != null? perspective.fieldOfView : 67.f;
            result = new CameraServer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fov);
        }
        result.setPosition(position);
        result.lookAt(lookAt);
        result.setUpVector(up);
        result.setRange(near, far);
        return result;
    }

    @Override
    protected void modify(IDLGameContext context, BaseCompMgr server) {
        CameraServer cameraServer = (CameraServer)server;
        if (perspective != null || orthographic != null) {
            boolean isPersp = cameraServer.isPerspective();
            if (isPersp && perspective != null) {
                if (perspective.fieldOfView != null) {
                    float fieldOfView = perspective.fieldOfView;
                    cameraServer.setFieldOfView(fieldOfView);
                }
            } else if (isPersp && orthographic != null) {
                cameraServer.setCamera(new OrthographicCamera());
            } else if (!isPersp && perspective != null) {
                float fieldOfView = perspective.fieldOfView != null? perspective.fieldOfView : 67.f;
                cameraServer.setCamera(new PerspectiveCamera());
                cameraServer.setFieldOfView(fieldOfView);
            } 
        }
        if (position != null) {
            cameraServer.setPosition(position);
        }
        if (lookAt != null) {
            cameraServer.lookAt(lookAt);
        }
        if (up != null) {
            cameraServer.setUpVector(up);
        }

        if (near != null || far != null) {
            if (near == null)
                near = cameraServer.getNear();
            if (far == null)
                far = cameraServer.getFar();
            cameraServer.setRange(near, far);
        }
    }
}
