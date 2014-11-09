package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.middleend.Transform;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class TransformTemplate extends IDLCompTemplate {
    public Vector3 position = null; 
    public Quaternion orientation = null;
    public Vector3 scale = null;

    @Override
    protected BaseComp create(IDLGameContext context) {
        if (position == null) 
            position = new Vector3();
        if (orientation == null)
            orientation = new Quaternion();
        if (scale == null)
            scale = new Vector3(1, 1, 1);
        return new Transform(position, orientation, scale);
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
        Transform transform = (Transform)comp;
        Matrix4 mat = transform.get();
        if (position == null) {
            position = new Vector3();
            mat.getTranslation(position);
        }

        if (orientation == null) {
            orientation = new Quaternion();
            mat.getRotation(orientation);
        }
        if (scale == null) {
            scale = new Vector3();
            mat.getScale(scale);
        }
        transform.modify().set(position, orientation, scale);
    }
}