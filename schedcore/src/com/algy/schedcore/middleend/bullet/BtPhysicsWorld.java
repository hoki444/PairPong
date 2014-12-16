package com.algy.schedcore.middleend.bullet;

import java.util.ArrayList;
import java.util.Iterator;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseSchedServer;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.SchedcoreRuntimeError;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.CollisionComp.CollisionInfo;
import com.algy.schedcore.util.IntegerBitmap;
import com.algy.schedcore.util.Lister;
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
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
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
                            if (isFirst)
                                mfp.getLocalPointA(posOut);
                            else
                                mfp.getLocalPointB(posOut);
                        }

                        @Override
                        public void otherLocalPosition(Vector3 posOut) {
                            if (isFirst)
                                mfp.getLocalPointB(posOut);
                            else
                                mfp.getLocalPointA(posOut);
                        }
                    };
                    idx++;
                    return info;
                }
            };
        }
    }
    private IntegerBitmap<GameItem> collBitmap = new IntegerBitmap<GameItem>();
    
    private btCollisionConfiguration collConfig;
    public btDynamicsWorld world;
    private btConstraintSolver ctrtSolver;
    private btCollisionDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private ContactListener contactListener;

    private Vector3 gravity;
    private long schedPeriod;
    private float worldTps;
    
    public int maxSubStep = 7;
    
    public static void initBullet () {
        // use reference counting & enable logging
        Bullet.init(true);
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
            try {
                int userVal0 = colObj0.getUserValue();
                int userVal1 = colObj1.getUserValue();
                if (collBitmap.has(userVal0) && collBitmap.has(userVal1)) {
                    GameItem item0, item1;
                    item0 = collBitmap.get(userVal0);
                    item1 = collBitmap.get(userVal1);
                    
                    if (item0.has(CollisionComp.class))
                        item0.as(CollisionComp.class).beginCollision(item1);
                    if (item1.has(CollisionComp.class))
                        item1.as(CollisionComp.class).beginCollision(item0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SchedcoreRuntimeError(e);
            }
        }

        @Override
        public void onContactEnded(btPersistentManifold manifold) {
            try {
                int userVal0 = manifold.getBody0().getUserValue();
                int userVal1 = manifold.getBody1().getUserValue();
                
                if (collBitmap.has(userVal0) && collBitmap.has(userVal1)) {
                    GameItem item0, item1;
                    item0 = collBitmap.get(userVal0);
                    item1 = collBitmap.get(userVal1);
                    
                    if (item0.has(CollisionComp.class)) {
                        item0.as(CollisionComp.class).endCollision(item1, new CollisionIterable(manifold, true));
                    }
                    if (item1.has(CollisionComp.class)) {
                        item1.as(CollisionComp.class).endCollision(item0, new CollisionIterable(manifold, false));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SchedcoreRuntimeError(e);
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
        for (GameItem gameItem : collBitmap) {
            Transform transform = gameItem.as(Transform.class);
            if (gameItem.has(BtColliderComp.class) && transform.isSyncToPhysicsRequired()) {
                gameItem.as(BtColliderComp.class).forceMove(transform.get());
                transform.notifySynced();
            }

            if (gameItem.has(BtRigidBodyComp.class)) {
                BtRigidBodyComp bodyComp = gameItem.as(BtRigidBodyComp.class);
                Vector3 forcedGravity = bodyComp.getForcedGravity();
                if (forcedGravity != null) {
                    bodyComp.getRigidBody().setGravity(forcedGravity);
                }
            }
        }

        // Simulate the physics world
        advanceWorld();
    }
    

    @Override
    public void beginSchedule() {
    }

    @Override
    public void endSchedule() {
    }

    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> sigs) {
        sigs.add(BtColliderComp.class);
    }
    
    private static class Operation {
        public btRigidBody rigidBody;
        public btCollisionObject collObject;
        public BtColliderComp colliderComp;
        public CollisionFilter filter;
    }

    private class DeferedWorldUpdater {
        public ArrayList<Operation> addition = new ArrayList<Operation>();
        public ArrayList<Operation> removal  = new ArrayList<Operation>();

        private boolean simulatingWorld = false;
        
        public void begin () {
            this.simulatingWorld = true;
        }

        public void end () {
            for (Operation op : addition) {
                if (op.rigidBody != null) {
                    if (op.filter != null) {
                        world.addRigidBody(op.rigidBody, op.filter.group, op.filter.mask);
                    } else {
                        world.addRigidBody(op.rigidBody);
                    }
                    op.rigidBody.release();
                }

                if (op.collObject != null) {
                    if (op.filter != null)
                        world.addCollisionObject(op.collObject, op.filter.group, op.filter.mask);
                    else
                        world.addCollisionObject(op.collObject);
                    op.collObject.release();
                }
                op.colliderComp.onAddedToWorld();
            }
            addition.clear();

            for (Operation op : removal) {
                if (op.rigidBody != null) {
                    world.removeRigidBody(op.rigidBody);
                    op.rigidBody.release();
                }
                if (op.collObject != null) {
                    world.removeCollisionObject(op.collObject);
                    op.collObject.release();
                }
            }
            removal.clear();

            this.simulatingWorld = false;
        }
        
        public void add(BtRigidBodyComp comp) {
            btRigidBody rigidBody = comp.getRigidBody();
            CollisionFilter collFilter = comp.getCollisionFilter();
            if (simulatingWorld) {
                rigidBody.obtain();
                Operation op = new Operation();
                op.rigidBody = rigidBody;
                op.colliderComp = comp;
                op.filter = collFilter;
                addition.add(op);
            } else {
                if (collFilter != null)
                    world.addRigidBody(rigidBody, collFilter.group, collFilter.mask);
                else
                    world.addRigidBody(rigidBody);

                if (comp.getForcedGravity() != null)
                    rigidBody.setGravity(comp.getForcedGravity());
                comp.onAddedToWorld();
            }
        }

        public void add(BtDetectorComp comp) {
            btCollisionObject collObj = comp.getCollObj();
            CollisionFilter collFilter = comp.getCollisionFilter();
            if (simulatingWorld) {
                collObj.obtain();
                Operation op = new Operation();
                op.collObject = collObj;
                op.colliderComp = comp;
                op.filter = collFilter;
                addition.add(op);
            } else {
                if (collFilter != null) {
                    world.addCollisionObject(collObj, collFilter.group, collFilter.mask);
                } else
                    world.addCollisionObject(collObj);
                comp.onAddedToWorld();
            }
        }

        public void remove(btRigidBody rigidBody) {
            if (simulatingWorld) {
                rigidBody.obtain();
                Operation op = new Operation();
                op.rigidBody = rigidBody;
                removal.add(op);
            } else {
                world.removeRigidBody(rigidBody);
            }
        }

        public void remove(btCollisionObject collObj) {
            if (simulatingWorld) {
                collObj.obtain();
                Operation op = new Operation();
                op.collObject = collObj;
                removal.add(op);
            } else {
                world.removeCollisionObject(collObj);
            }
        }
    }
    
    private DeferedWorldUpdater updater = new DeferedWorldUpdater();

    @Override
    public void hookAddComp(BaseComp comp) {
        if (comp instanceof BtColliderComp) {
            BtColliderComp ccomp = (BtColliderComp) comp;
            int collId = collBitmap.add((GameItem)ccomp.owner());
            ccomp.setCollId(collId);
            if (ccomp instanceof BtRigidBodyComp) {
                btRigidBody rigidBody = ((BtRigidBodyComp)comp).getRigidBody();
                rigidBody.setUserValue(collId);
                updater.add((BtRigidBodyComp)comp);
            } else if (ccomp instanceof BtDetectorComp) {
                btCollisionObject collObject = ((BtDetectorComp)comp).getCollObj();
                collObject.setUserValue(collId);
                updater.add(((BtDetectorComp)comp)); //TODO
            }
        } 
    }
    
    

    @Override
    public void hookRemoveComp(BaseComp comp) {
        if (comp instanceof BtColliderComp) {
            BtColliderComp ccomp = (BtColliderComp) comp;
            collBitmap.remove(ccomp.getCollId());
            if (ccomp instanceof BtRigidBodyComp) {
                btRigidBody rigidBody = ((BtRigidBodyComp)comp).getRigidBody();
                rigidBody.setUserValue(-1);
                updater.remove(rigidBody);
            } else if (ccomp instanceof BtDetectorComp) {
                btCollisionObject collObject = ((BtDetectorComp)comp).getCollObj();
                collObject.setUserValue(-1);
                updater.remove(collObject);
            }
        }
    }

    private void advanceWorld() {
        updater.begin();
        world.stepSimulation(this.worldTps, maxSubStep);
        updater.end();
    }
    

    @Override
    protected void onAdhered() {
        collConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(this.collConfig);
        broadphase = new btDbvtBroadphase();
        ctrtSolver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld(dispatcher, 
                                                 broadphase,
                                                 ctrtSolver,
                                                 collConfig);
        world.setGravity(gravity);
        contactListener = new PhysicsContactListner();

        collConfig.obtain();
        dispatcher.obtain();
        broadphase.obtain();
        ctrtSolver.obtain();
        world.obtain();
        contactListener.obtain();
    }

    @Override
    protected void onDetached() {
        collConfig.release();
        dispatcher.release();
        broadphase.release();
        ctrtSolver.release();
        world.release();
        contactListener.release();
    }

    public Vector3 getGravity() {
        return this.world.getGravity();
    }

    public void setGravity(Vector3 gravity) {
        this.world.setGravity(gravity);
    }
}