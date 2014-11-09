package com.algy.schedcore.frontend.idl.reflection;

public class SelectiveGroup extends MetaReflectionField {
    String [] fieldNames;
    public String selectedField;
    public boolean unspecifiable;

    public SelectiveGroup (String ... fieldNames) {
        this.fieldNames = fieldNames;
        this.unspecifiable = false;
    }
    public SelectiveGroup (boolean unsepcifiable, String ... fieldNames) {
        this.fieldNames = fieldNames;
        this.unspecifiable = unsepcifiable;
    }
}
