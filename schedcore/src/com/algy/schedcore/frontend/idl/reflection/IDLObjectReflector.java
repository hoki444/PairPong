package com.algy.schedcore.frontend.idl.reflection;

import java.lang.reflect.Field;

import com.algy.schedcore.frontend.idl.IDLValue;

interface IDLPrimitiveReflector {
    public void parseAndSet (IDLValue value, Field field, Object obj);
}

public interface IDLObjectReflector {
    public Object parse (IDLValue value, Class<?> cls);
}