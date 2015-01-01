package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompServerTemplate;
import com.algy.schedcore.frontend.idl.reflection.NotModifiable;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.badlogic.gdx.math.Vector3;

public class BtPhysicsWorldTemplate extends IDLCompServerTemplate {
    public static NotModifiable req = new NotModifiable("maxSubStep", "updatePeriod");

    public Long updatePeriod = null;
    public Integer maxSubStep = null;
    public Vector3 gravity = null;
    @Override
    protected BaseCompMgr create(IDLGameContext context) {
        if (gravity == null)
            gravity = new Vector3 (0, -9.8f, 0);

        BtPhysicsWorld world;
        
        if (updatePeriod != null) {
            world = new BtPhysicsWorld(gravity, updatePeriod);
        } else 
            world = new BtPhysicsWorld(gravity);

        if (maxSubStep != null) {
            world.maxSubStep = maxSubStep;
        } 
        return world;
    }

    @Override
    protected void modify(IDLGameContext context, BaseCompMgr server) {
        if (gravity != null)
            ((BtPhysicsWorld)server).setGravity(gravity);
    }

}
