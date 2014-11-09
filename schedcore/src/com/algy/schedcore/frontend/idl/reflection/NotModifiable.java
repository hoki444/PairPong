package com.algy.schedcore.frontend.idl.reflection;

public class NotModifiable extends MetaReflectionField {
    public String [] fieldNames;
    public NotModifiable (String ...fieldNames) {
        this.fieldNames = fieldNames;
    }
}
