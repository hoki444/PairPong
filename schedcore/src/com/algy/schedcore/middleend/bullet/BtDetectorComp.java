package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.middleend.Transform;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BtDetectorComp extends BtColliderComp {
    private btCollisionShape shape;
    private btCollisionObject collObj;
    private CollisionFilter collFilter;
    public BtDetectorComp (btCollisionShape shape, CollisionFilter collFilter) {
        this.shape = shape;
        this.collObj = new btCollisionObject();
        this.collObj.setCollisionShape(shape);
        this.collFilter = collFilter;

        this.shape.obtain(); 
        this.collObj.obtain();
    }
    
    public BtDetectorComp (btCollisionShape shape) {
        this(shape, (CollisionFilter)null);
    }

    @Override
    public BaseComp duplicate() {
        return new BtDetectorComp(shape, collFilter);
    }
    

    @Override
    protected void onAttached() {
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
        Matrix4 dest = new Matrix4(mat);
        float sx, sy, sz; // don't want to apply scale factors.
    	sx = mat.getScaleX();
    	sy = mat.getScaleY();
    	sz = mat.getScaleZ();
    	
        dest.scale(1 / sx, 
        		   1 / sy, 
        		   1 / sz);
        this.collObj.setWorldTransform(dest);
    }

    @Override
    public Matrix4 getTransform() {
        return this.collObj.getWorldTransform();
    }

    @Override
    public void onAddedToWorld() {
    }
    
    public CollisionFilter getCollisionFilter() {
        return collFilter;
    }
}