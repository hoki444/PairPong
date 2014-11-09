package com.algy.schedcore.frontend.idl.reflection;

import com.algy.schedcore.frontend.idl.IDLValue;

public abstract class IDLObjectReflectorTemplate extends IDLTemplateMixin {
    public abstract Object make ();
    
    public static IDLObjectReflector makeReflector (final Class<? extends IDLObjectReflectorTemplate> template) {
        return new IDLObjectReflector() {
            @Override
            public Object parse(IDLValue value, Class<?> cls) {
                IDLObjectReflectorTemplate self = 
                        (IDLObjectReflectorTemplate) IDLReflector.parse(value, template, true);
                return self.make();
            }
        };
    }
}