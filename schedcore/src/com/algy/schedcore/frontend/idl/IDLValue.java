package com.algy.schedcore.frontend.idl;

import java.util.Map;


public class IDLValue {
    protected static class ClassName {
        public ClassName(String name) {
            this.name = name;
        }
        public String name;
    }

    private Object objBox;
    
    private IDLValue (Object objBox) {
        this.objBox = objBox;
    }
    
    public boolean isInteger () {
        return objBox instanceof Integer;
    }

    public boolean isFloat () {
        return objBox instanceof Float;
    }
    
    public boolean isString () {
        return objBox instanceof String;
    }
    
    public boolean isClassName () {
        return objBox instanceof ClassName;
    }
    
    public boolean isDict () {
        return objBox instanceof Map;
    }
    
    public boolean isBoolean () {
        return objBox instanceof Boolean;
    }
    
    public boolean isNull () {
        return objBox == null;
    }
    
    public int asInteger () {
        return (Integer)objBox;
    }
    
    public float asFloat () {
        return (Float)objBox;
    }
    
    public String asString () {
        return (String)objBox;
    }
    
    public String asClassName () {
        return ((ClassName)objBox).name;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, IDLValue> asDict () {
        return (Map<String, IDLValue>)objBox;
    }
    
    public boolean asBoolean() {
        return (Boolean)objBox;
    }
    
    public static IDLValue createInteger (int i) {
        return new IDLValue(i);
    }
    
    public static IDLValue createFloat (float f) {
        return new IDLValue(f);
    }
    
    public static IDLValue createString (String s) {
        return new IDLValue(s);
    }

    public static IDLValue createClassName (String clsName) {
        return new IDLValue(new ClassName(clsName));
    }

    public static IDLValue createDict (Map<String, IDLValue> dict) {
        return new IDLValue(dict);
    }   
    
    public static IDLValue createBoolean (boolean b) {
        return new IDLValue (b);
    }
    
    public static IDLValue createNull () {
        return new IDLValue(null);
    }
}

