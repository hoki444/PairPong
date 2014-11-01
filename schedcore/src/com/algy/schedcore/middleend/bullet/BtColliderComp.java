package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public abstract class BtColliderComp extends BaseComp {
    private int collId;

    
    public int getCollId() {
        return collId;
    }
    
    public void setCollId(int collId) {
        this.collId = collId;
    }
    
    // Caller of the method just borrow the ownership to physics server
    public abstract btCollisionShape getShape();
    public abstract Matrix4 getTransform ();
    public abstract void forceMove (Matrix4 mat);
}