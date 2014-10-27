package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BtCollObjComp extends BtColliderComp {
    // XXX: this version must lead to memory leak in Bullet's C++ side. Use refcount.

    private btCollisionShape shape;
    private btCollisionObject collObj;
    
    public BtCollObjComp (btCollisionShape shape) {
        this.shape = shape;
        this.collObj = new btCollisionObject();
        this.collObj.setCollisionShape(shape);

        shape.obtain(); 
        this.collObj.obtain();
    }

    @Override
    public IComp duplicate() {
        return new BtCollObjComp(shape);
    }
    

    @Override
    protected void onAdhered() {
        this.collObj.setWorldTransform(owner().as(Transform.class).get());
    }

    @Override
    protected void onDetached() {
        shape.release();
        collObj.release();
    }

    @Override
    public btCollisionShape getShape() {
        return shape;
    }

    public btCollisionObject getCollObj () {
        return collObj;
    }

    @Override
    public void forceMove(Matrix4 mat) {
        this.collObj.setWorldTransform(mat);
    }

    @Override
    public Matrix4 getTransform() {
        return this.collObj.getWorldTransform();
    }
    
}