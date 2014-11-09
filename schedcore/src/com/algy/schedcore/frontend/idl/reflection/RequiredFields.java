package com.algy.schedcore.frontend.idl.reflection;

public class RequiredFields extends MetaReflectionField {
    // Meta class used as meta field in 
    public String [] fieldNames;

    public RequiredFields (String ... fieldNames) {
        this.fieldNames = fieldNames;
    }
}
