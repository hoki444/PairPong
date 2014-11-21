package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.IDLLoadError;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;

public class EmptyCompTemplate extends IDLCompTemplate {
    private Class<? extends BaseComp> destClass;
    public EmptyCompTemplate (Class<? extends BaseComp> destClass) {
        this.destClass = destClass;
    }

    @Override
    protected BaseComp create(IDLGameContext context) {
        try {
            Object obj = destClass.getDeclaredConstructor().newInstance();
            return (BaseComp)obj;
        } catch (Exception e) {
            // catch 'em all!
            throw new IDLLoadError(e);
        }
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
    }

}