package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.NotModifiable;
import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.middleend.bullet.BtDetectorComp;

public class BtDetectorTemplate extends IDLCompTemplate {
    public static RequiredFields req = new RequiredFields("shape");
    public static NotModifiable nm = new NotModifiable("shape");
    public ShapeTemplate shape;
    @Override
    protected BaseComp create(IDLGameContext context) {
        return new BtDetectorComp(shape.makeShape());
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) { }

}
