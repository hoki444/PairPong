package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.IComp;
import com.algy.schedcore.middleend.Transform;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class BtRigidBodyComp extends BtColliderComp {
    private class TransformSynchronizer extends btMotionState {
        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
        }
        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
        	/*
        	 * NOTE: 
        	 */
            owner().as(Transform.class).syncFromPhysics(worldTrans);
        }
    }

    private btRigidBody body;
    private btCollisionShape shape;
    private float mass;
    private Vector3 localInertia;
    private btMotionState motionState;
    
    private Vector3 forcedGravity = null;
    private CollisionFilter collFilter;
    
    private BtRigidBodyComp (btCollisionShape shape,
                             float mass,
                             Vector3 localInertia,
                             int addFlag,
                             CollisionFilter collFilter) {
        motionState = new TransformSynchronizer();
        this.shape = shape;
        this.collFilter = collFilter;
        this.mass = mass;
        if (localInertia == null) {
            localInertia = new Vector3();
            if (mass > 0) shape.calculateLocalInertia(mass, localInertia);
        }
        this.localInertia = localInertia;
            
        motionState = new TransformSynchronizer();
        body = new btRigidBody(mass,
                               motionState,
                               shape,
                               localInertia);
        body.setCollisionFlags(body.getCollisionFlags() | 
                               addFlag | 
                               btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        this.shape.obtain();
        body.obtain();
        motionState.obtain();
    }
    
    public static BtRigidBodyComp staticBody (btCollisionShape shape, CollisionFilter collFilter) {
        return new BtRigidBodyComp(shape, 0, null, 0, collFilter);
    }

    public static BtRigidBodyComp staticBody (btCollisionShape shape) {
        return BtRigidBodyComp.staticBody(shape, null);
    }
    
    public static BtRigidBodyComp kinematicBody (btCollisionShape shape, CollisionFilter collFilter) {
    	BtRigidBodyComp result = new BtRigidBodyComp(shape, 0, null, CollisionFlags.CF_KINEMATIC_OBJECT, collFilter);
    	
    	return result;
    }
    public static BtRigidBodyComp kinematicBody (btCollisionShape shape) {
    	return kinematicBody(shape, null);
    }
    
    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass) {
        return dynamicBody(shape, mass, null, null);
    }

    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass, CollisionFilter collFilter) {
        return dynamicBody(shape, mass, null, collFilter);
    }

    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass, Vector3 localInertia) {
        return dynamicBody(shape, mass, localInertia, null);
    }

    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass, Vector3 localInertia, CollisionFilter collFilter) {
        BtRigidBodyComp result = new BtRigidBodyComp(shape, mass, localInertia, 0, collFilter);
        return result;
    }

    public btRigidBody getRigidBody() {
        return body;
    }
    
    
    @Override
    public void onAddedToWorld() {
        if (forcedGravity != null) {
            body.setGravity(forcedGravity);
        }
    }


    @Override
    public IComp duplicate() {
        BtRigidBodyComp result = new BtRigidBodyComp(shape, 
                                   getMass(), 
                                   new Vector3(getLocalInertia()), 
                                   body.getCollisionFlags(),
                                   collFilter);
        result.setAngularVelocity(getAngularVelocity())
              .setLinearVelocity(getLinearVelocity())
              .setLinearDamping(getLinearDamping())
              .setAngularDamping(getAngularDamping())
              .setFriction(getFriction())
              .setRestitution(getRestitution())
              .setRollingFriction(getRollingFriction())
              .forceGravity(getForcedGravity());
        return result;
    }

    
    @Override
    protected void onAdhered() {
        Transform tr = owner().as(Transform.class);
        forceMove(tr.get());
        tr.notifySynced();
        
    }

    @Override
    protected void onDetached() {
        motionState.release();
        body.setMotionState(null);
        body.release();
        shape.release();
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
        body.proceedToTransform(dest);
        body.activate();
    }


    @Override
    public Matrix4 getTransform() {
        return this.body.getWorldTransform();
    }

    public BtRigidBodyComp activate() {
        body.activate();
        return this;
    }

    /*
     * Below functions are just getter functions each of which returns coresponding arugment of constructor
     */
    @Override
    public btCollisionShape getShape() {
        return shape;
    }

    public float getMass() {
        return mass;
    }
    
    public Vector3 getLocalInertia() {
        return localInertia;
    }

    public CollisionFilter getCollisionFilter () {
        return collFilter;
    }

    /*
     * Below functions are general getter/setter of common attributes of rigid body, 
     * used to duplicate this component.
     */
    public float getRestitution() {
        return this.body.getRestitution();
    }
    public Vector3 getLinearVelocity() {
        return body.getLinearVelocity();
    }
    public Vector3 getAngularVelocity() {
        return this.body.getAngularVelocity();
    }

    public float getLinearDamping () {
        return body.getLinearDamping();
    }

    public float getAngularDamping () {
        return body.getAngularDamping();
    }
    public float getRollingFriction() {
        return this.body.getRollingFriction();
    }
    public float getFriction() {
        return this.body.getFriction();
    }

    public Vector3 getForcedGravity() {
        return forcedGravity;
    }


    public BtRigidBodyComp setRestitution(float rest) {
        this.body.setRestitution(rest);
        return this;
    }
    public BtRigidBodyComp setLinearVelocity(Vector3 vel) {
        body.setLinearVelocity(vel);
        return this;
    }
    public BtRigidBodyComp setLinearDamping(float damping) {
        body.setDamping(damping, getAngularDamping());
        return this;
    }
    public BtRigidBodyComp setAngularDamping(float damping) {
        body.setDamping(getLinearDamping(), damping);
        return this;
    }
    public BtRigidBodyComp setAngularVelocity(Vector3 ang_vel) {
        this.body.setAngularVelocity(ang_vel);
        return this;
    }
    public BtRigidBodyComp setRollingFriction(float frict) {
        this.body.setRollingFriction(frict);
        return this;
    }

    public BtRigidBodyComp setFriction(float frict) {
        this.body.setFriction(frict);
        return this;
    }
    
    public BtRigidBodyComp forceGravity(Vector3 g) {
        forcedGravity = g;
        return this;
    }
    
}
