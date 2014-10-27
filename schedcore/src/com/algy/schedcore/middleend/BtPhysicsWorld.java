package com.algy.schedcore.middleend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseSchedServer;
import com.algy.schedcore.IntegerBitmap;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.middleend.CollisionComp.CollisionInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;


public class BtPhysicsWorld extends BaseSchedServer {
    private static class CollisionIterable implements Iterable<CollisionInfo> {
        private btPersistentManifold manifold;
        private boolean isFirst;
        public CollisionIterable (btPersistentManifold manifold, boolean isFirst) {
            this.manifold = manifold;
            this.isFirst = isFirst;
        }

        @Override
        public Iterator<CollisionInfo> iterator() {
            return new Iterator<CollisionInfo>() {
                private int idx = 0;
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public boolean hasNext() {
                    return idx < manifold.getNumContacts();
                }
                @Override
                public CollisionInfo next() {
                    CollisionInfo info = new CollisionInfo() {
                        private btManifoldPoint mfp = manifold.getContactPoint(idx);
                        @Override
                        public void thisPosition(Vector3 posOut) {
                            if (isFirst)
                                mfp.getPositionWorldOnA(posOut);
                            else
                                mfp.getPositionWorldOnB(posOut);
                        }
                        
                        @Override
                        public void otherPosition(Vector3 posOut) {
                            if (isFirst)
                                mfp.getPositionWorldOnB(posOut);
                            else
                                mfp.getPositionWorldOnA(posOut);
                        }
                        @Override
                        public float getImpulse() {
                            return mfp.getAppliedImpulse();
                        }

                        @Override
                        public void thisLocalPosition(Vector3 posOut) {
                            // TODO Auto-generated method stub
                            
                        }

                        @Override
                        public void otherLocalPosition(Vector3 posOut) {
                            // TODO Auto-generated method stub
                            
                        }
                    };
                    idx++;
                    return info;
                }
            };
        }
    }
    private IntegerBitmap<BtColliderComp> collBitmap = new IntegerBitmap<BtColliderComp>();
    
    private btCollisionConfiguration collConfig;
    private btDynamicsWorld world;
    private btConstraintSolver ctrtSolver;
    private btCollisionDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private ContactListener contactListener;

    private Vector3 gravity;
    private long schedPeriod;
    private float worldTps;
    
    public int maxSubStep = 3;
    
    public static void initBullet () {
        // use reference counting & disable logging
        Bullet.init();
    }

    public BtPhysicsWorld (Vector3 gravity, long schedPeriod, long worldPeriod) {
        this.gravity = gravity;
        this.schedPeriod = schedPeriod;
        this.worldTps = 0.001f * worldPeriod;
    }
    
    public BtPhysicsWorld (Vector3 gravity, long period) {
        this(gravity, period, period);
    }
    public BtPhysicsWorld (Vector3 gravity) {
        this(gravity, 30);
    }

    private class PhysicsContactListner extends ContactListener {
        @Override
        public void onContactStarted(btCollisionObject colObj0,
                btCollisionObject colObj1) {
            int userVal0 = colObj0.getUserValue();
            int userVal1 = colObj1.getUserValue();
            if (collBitmap.has(userVal0) && collBitmap.has(userVal1)) {
                BtColliderComp ccomp0, ccomp1;
                ccomp0 = collBitmap.get(userVal0);
                ccomp1 = collBitmap.get(userVal1);
                
                if (ccomp0.owner().has(CollisionComp.class))
                    ccomp0.owner().as(CollisionComp.class).beginCollision((GameItem)(ccomp1.owner()));
                if (ccomp1.owner().has(CollisionComp.class))
                    ccomp1.owner().as(CollisionComp.class).beginCollision((GameItem)(ccomp0.owner()));
            }
        }

        @Override
        public void onContactEnded(btPersistentManifold manifold) {
            int userVal0 = manifold.getBody0().getUserValue();
            int userVal1 = manifold.getBody1().getUserValue();
            
            if (collBitmap.has(userVal0) && collBitmap.has(userVal1)) {
                BtColliderComp ccomp0, ccomp1;
                ccomp0 = collBitmap.get(userVal0);
                ccomp1 = collBitmap.get(userVal1);
                
                if (ccomp0.owner().has(CollisionComp.class)) {
                    ccomp0.owner().as(CollisionComp.class).endCollision((GameItem)(ccomp1.owner()),
                            new CollisionIterable(manifold, true));
                }
                if (ccomp1.owner().has(CollisionComp.class))
                    ccomp1.owner().as(CollisionComp.class).endCollision((GameItem)(ccomp0.owner()), 
                            new CollisionIterable(manifold, false));
            }
        }
    }
    
    @Override
    public long schedPeriod() {
        return schedPeriod;
    }

    @Override
    public long schedOffset() {
        return 0;
    }

    @Override
    public void schedule(SchedTime time) {
        // Gather transforms to synchronize this component from Transform first.
        // XXX: IMPROVE ME
        for (BtColliderComp comp : collBitmap) {
            Transform transform = comp.owner().as(Transform.class);
            if (transform.isSyncToPhysicsRequired()) {
                comp.forceMove(transform.get());
                transform.notifySynced();
            }
        }
        // Simulate the physics world
        world.stepSimulation(this.worldTps, maxSubStep);
    }

    @Override
    public void beginSchedule() {
    }

    @Override
    public void endSchedule() {
    }

    @Override
    public List<Class<? extends BaseComp>> hookFilters() {
        ArrayList<Class<? extends BaseComp>> res = new ArrayList<Class<? extends BaseComp>>();
        res.add(BtColliderComp.class);
        return res;
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        if (comp instanceof BtColliderComp) {
            BtColliderComp ccomp = (BtColliderComp) comp;
            int collId = collBitmap.add(ccomp);
            ccomp.setCollId(collId);
            if (ccomp instanceof BtRigidBodyComp) {
                this.world.addRigidBody(((BtRigidBodyComp)comp).getRigidBody());
                ((BtRigidBodyComp)comp).getRigidBody().setUserValue(collId);
            } else if (ccomp instanceof BtCollObjComp) {
                this.world.addCollisionObject(((BtCollObjComp)comp).getCollObj());
                ((BtCollObjComp)comp).getCollObj().setUserValue(collId);
            }
        }
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        if (comp instanceof BtColliderComp) {
            BtColliderComp ccomp = (BtColliderComp) comp;
            collBitmap.remove(ccomp.getCollId());
            if (ccomp instanceof BtRigidBodyComp) {
                this.world.removeRigidBody(((BtRigidBodyComp)comp).getRigidBody());
            } else if (ccomp instanceof BtCollObjComp) {
                this.world.removeCollisionObject(((BtCollObjComp)comp).getCollObj());
            }
        }
    }

    @Override
    protected void onAdhered() {
        this.collConfig = new btDefaultCollisionConfiguration();
        this.dispatcher = new btCollisionDispatcher(this.collConfig);
        this.broadphase = new btDbvtBroadphase();
        this.ctrtSolver = new btSequentialImpulseConstraintSolver();
        this.world = new btDiscreteDynamicsWorld(dispatcher, 
                                                 broadphase,
                                                 ctrtSolver,
                                                 collConfig);
        this.world.setGravity(gravity);
        this.contactListener = new PhysicsContactListner();

        this.collConfig.obtain();
        this.dispatcher.obtain();
        this.ctrtSolver.obtain();
        this.broadphase.obtain();
        this.world.obtain();
        this.contactListener.obtain();
    }

    @Override
    protected void onDetached() {
        this.collConfig.release();
        this.dispatcher.release();
        this.broadphase.release();
        this.ctrtSolver.release();
        this.world.release();
        this.contactListener.release();
    }
}
