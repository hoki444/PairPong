package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.frontend.idl.reflection.IDLObjectReflectorTemplate;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Matrix4ObjTemplate extends IDLObjectReflectorTemplate {
    public Vector3 position = new Vector3();
    public Quaternion orientation = new Quaternion();
    public Vector3 scale = new Vector3(1, 1, 1);

    @Override
    public Object make() {
        return new Matrix4(position, orientation, scale);
    }

}
