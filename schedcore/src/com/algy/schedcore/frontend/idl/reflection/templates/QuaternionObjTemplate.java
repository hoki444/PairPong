package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.frontend.idl.reflection.IDLObjectReflectorTemplate;
import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.frontend.idl.reflection.SelectiveGroup;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class QuaternionObjTemplate extends IDLObjectReflectorTemplate {
    private static class Euler {
        public float yaw;
        public float pitch;
        public float roll;
    }
    private static class AxisRotation {
        public static RequiredFields req = new RequiredFields("axis");
        public Vector3 axis;
        public float angle;
    }

    static SelectiveGroup group = new SelectiveGroup("euler", "axisRotation");
    
    public Euler euler;
    public AxisRotation axisRotation;

    @Override
    public Object make() {
        if (euler != null) {
            return new Quaternion().setEulerAngles(euler.yaw, euler.pitch, euler.roll);
        } else { // else if (axisRotation != null) {
            return new Quaternion(axisRotation.axis, axisRotation.angle);
        }
    }

}