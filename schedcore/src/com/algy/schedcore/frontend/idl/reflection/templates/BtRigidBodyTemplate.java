package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.NotModifiable;
import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class BtRigidBodyTemplate extends IDLCompTemplate {
    // fields
    static NotModifiable nm = new NotModifiable("shape", "mass", "isKinematic", "localIntertia");
    static RequiredFields req = new RequiredFields("shape");
    
    public ShapeTemplate shape;
    public float mass = 0;
    public boolean isKinematic = false;

    public Vector3 localIntertia = null;
    public Vector3 linearVelocity = null;
    public Vector3 angularVelocity = null;
    public Float linearDamping = null;
    public Float angularDamping = null;
    public Float friction = null;
    public Float rollingFriction = null;
    public Float restitution = null;


    public void modifyMisc(BtRigidBodyComp rigidbodyComp) {
        if (linearVelocity != null)
            rigidbodyComp.setLinearVelocity(linearVelocity);
        if (angularVelocity != null)
            rigidbodyComp.setAngularVelocity(angularVelocity);
        if (linearDamping != null)
            rigidbodyComp.setLinearDamping(linearDamping);
        if (angularDamping != null)
            rigidbodyComp.setAngularDamping(angularDamping);
        if (friction != null)
            rigidbodyComp.setFriction(friction);
        if (rollingFriction != null)
            rigidbodyComp.setRollingFriction(rollingFriction);
        if (restitution != null)
            rigidbodyComp.setRestitution(restitution);
    }

    @Override
    protected BaseComp create(IDLGameContext context) {
        BtRigidBodyComp result;
        btCollisionShape btShape = shape.makeShape ();
        if (mass == 0) {
            if (isKinematic)
                result = BtRigidBodyComp.staticBody(btShape);
            else
                result = BtRigidBodyComp.kinematicBody(btShape);
        } else if (localIntertia != null) {
            result = BtRigidBodyComp.dynamicBody(btShape, mass, localIntertia);
        } else
            result = BtRigidBodyComp.dynamicBody(btShape, mass);
        modifyMisc(result);
        return result;
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
        modifyMisc((BtRigidBodyComp)comp);
    }
}
