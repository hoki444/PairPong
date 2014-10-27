package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.IComp;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Transform extends BaseComp {
    private Matrix4 mat;

    private boolean syncRequired = true; // synchronization to physical component is required
    

    public Transform (float x, float y, float z) {
        this(new Vector3(x, y, z), new Quaternion(), new Vector3(1, 1, 1));
    }
    
    public Transform (Vector3 pos) {
        this(pos, new Quaternion(), new Vector3(1, 1, 1));
    }
    
    public Transform (Vector3 pos, Quaternion ori) {
        this(pos, ori, new Vector3(1, 1, 1));
    }
    
    public Transform (Vector3 pos, Quaternion ori, Vector3 scale) {
        this.mat = new Matrix4(pos, ori, scale);
    }
    
    public Transform (Matrix4 mat) {
        this.mat = mat;
    }

    public Matrix4 modify() {
        this.syncRequired = true;
        return mat;
    }
    
    public Matrix4 get() {
        return mat;
    }
    
    public boolean isSyncToPhysicsRequired () {
        return syncRequired;
    }
    
    public void notifySynced ( ) {
        this.syncRequired = false;
    }
    
    public void syncFromPhysics(Matrix4 mat) {
        this.mat.set(mat);
        this.syncRequired = false;
    }


    public IComp duplicate() {
        return new Transform(new Matrix4(mat));
    }
    
    @Override
    protected void onAdhered() { }

    @Override
    protected void onDetached() { }

    public Quaternion getRotation(Quaternion rotation) {
        return this.mat.getRotation(rotation);
    }

    public Vector3 getScale(Vector3 scale) {
        return this.mat.getScale(scale);
    }

    public Vector3 getTranslation(Vector3 position) {
        return this.mat.getTranslation(position);
    }

}