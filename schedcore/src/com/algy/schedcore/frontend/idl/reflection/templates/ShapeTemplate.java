package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.frontend.idl.reflection.SelectiveGroup;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBox2dShape;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btMultiSphereShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class ShapeTemplate {
    public static class MultiSphereItem {
        public static RequiredFields req = new RequiredFields("position", "radius");
        public Vector3 position;
        public float raidus;
    }
    public static class RadiusAndHeight {
        public static RequiredFields req = new RequiredFields("height", "radius");
        public float radius;
        public float height;
    }
    
    public static class CompoundItem {
        public static RequiredFields req = new RequiredFields("shape", "transform");
        ShapeTemplate shape;
        Matrix4 transform;
    }

    public static SelectiveGroup _group = 
            new SelectiveGroup("box", 
                               "box2d", 
                               "capsule",
                               "compound",
                               "cone",
                               "convexhull",
                               "cylinder",
                               "multisphere",
                               "sphere");
    public Vector3 box;
    public Vector3 box2d;
    public ShapeTemplate.RadiusAndHeight capsule;
    public ShapeTemplate.CompoundItem [] compound;
    public ShapeTemplate.RadiusAndHeight cone;
    public Vector3[] convexhull;
    public Vector3 cylinder;
    public ShapeTemplate.MultiSphereItem [] multisphere;
    public Float sphere;

    public btCollisionShape makeShape () {
        if (box != null) {
            return new btBoxShape(box);
        } else if (box2d != null) {
            return new btBox2dShape(box2d);
        } else if (compound != null) {
            btCompoundShape cs = new btCompoundShape();
            for (ShapeTemplate.CompoundItem item : compound)
                cs.addChildShape(item.transform, item.shape.makeShape ());
            cs.recalculateLocalAabb();
            return cs;
        } else if (cone != null) {
            return new btConeShape(cone.radius, cone.height);
        } else if (convexhull != null) {
            btConvexHullShape ch = new btConvexHullShape();
            for (Vector3 pt : convexhull) {
                ch.addPoint(pt);
            }
            ch.recalcLocalAabb();
            return ch;
        } else if (cylinder != null) {
            return new btCylinderShape(cylinder);
        } else if (multisphere != null) {
            int len = multisphere.length;
            float [] radi = new float[len];
            Vector3 [] positions = new Vector3[len];

            for (int idx = 0; idx < len; idx++) {
                MultiSphereItem msi = multisphere[idx];
                radi[idx] = msi.raidus;
                positions[idx] = msi.position;
            }
            return new btMultiSphereShape(positions, radi, len);
        } else if (sphere != null) {
            return new btSphereShape(sphere);
        } 
        return null;
    }
}