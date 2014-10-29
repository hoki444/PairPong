package com.algy.schedcore.middleend;

import com.algy.schedcore.IComp;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class BtRigidBodyComp extends BtColliderComp {
    private class TransformSynchronizer extends btMotionState {
        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
        }
        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            owner().as(Transform.class).syncFromPhysics(worldTrans);
        }
    }

    private btRigidBody body;
    private btCollisionShape shape;
    private float mass;
    private Vector3 localInertia;
    private btMotionState motionState;
    
    private BtRigidBodyComp (btCollisionShape shape,
                             float mass,
                             Vector3 localInertia,
                             int addFlag) {
        shape.obtain();
        motionState = new TransformSynchronizer();
        this.shape = shape;
        this.mass = mass;
        if (localInertia == null) {
            localInertia = new Vector3();
            if (mass > 0) shape.calculateLocalInertia(mass, localInertia);
        }
        this.localInertia = localInertia;
            
        this.motionState = new TransformSynchronizer();
        body = new btRigidBody(mass,
                               motionState,
                               shape,
                               localInertia);
        body.setCollisionFlags(body.getCollisionFlags() | 
                               addFlag | 
                               btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
    }

    public static BtRigidBodyComp staticBody (btCollisionShape shape) {
        return new BtRigidBodyComp(shape, 0, null, 0);
    }
    
    public static BtRigidBodyComp kinematicBody (btCollisionShape shape) {
        return new BtRigidBodyComp(shape, 0, null, CollisionFlags.CF_KINEMATIC_OBJECT);
    }
    
    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass) {
        return dynamicBody(shape, mass, null);
    }

    public static BtRigidBodyComp dynamicBody (btCollisionShape shape, float mass, Vector3 localInertia) {
        BtRigidBodyComp result = new BtRigidBodyComp(shape, mass, localInertia, 0);
        return result;
    }

    public btRigidBody getRigidBody() {
        return body;
    }

    @Override
    public IComp duplicate() {
        BtRigidBodyComp result = new BtRigidBodyComp(shape, 
                                   getMass(), 
                                   new Vector3(getLocalInertia()), 
                                   body.getCollisionFlags());
        result.setAngularVelocity(getAngularVelocity())
              .setLinearVelocity(getLinearVelocity())
              .setFriction(getFriction())
              .setRestitution(getRestitution())
              .setRollingFriction(getRollingFriction());
        return result;
    }

    
    @Override
    protected void onAdhered() {
        Transform tr = owner().as(Transform.class);
        body.proceedToTransform(tr.get());
        tr.notifySynced();
    }

    @Override
    protected void onDetached() {
        motionState.release();
        body.release();
    }

    @Override
    public void forceMove(Matrix4 mat) {
        body.activate();
        this.body.proceedToTransform(mat);
    }


    @Override
    public Matrix4 getTransform() {
        return this.body.getWorldTransform();
    }
    public void activate() {
        this.body.activate();
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
    public float getRollingFriction() {
        return this.body.getRollingFriction();
    }
    public float getFriction() {
        return this.body.getFriction();
    }


    public BtRigidBodyComp setRestitution(float rest) {
        this.body.setRestitution(rest);
        return this;
    }
    public BtRigidBodyComp setLinearVelocity(Vector3 vel) {
        body.setLinearVelocity(vel);
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
}