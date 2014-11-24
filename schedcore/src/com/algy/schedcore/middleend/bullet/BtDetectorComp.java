package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.IComp;
import com.algy.schedcore.middleend.Transform;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BtDetectorComp extends BtColliderComp {
    private btCollisionShape shape;
    private btCollisionObject collObj;
    
    public BtDetectorComp (btCollisionShape shape) {
        this.shape = shape;
        this.collObj = new btCollisionObject();
        this.collObj.setCollisionShape(shape);

        this.shape.obtain(); 
        this.collObj.obtain();
    }

    @Override
    public IComp duplicate() {
        return new BtDetectorComp(shape);
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