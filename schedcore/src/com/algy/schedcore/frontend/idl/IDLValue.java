package com.algy.schedcore.frontend.idl;

import java.util.Map;


public class IDLValue {
    public static enum Type {
        Integer, 
        Float, 
        String, 
        Dict,
        Boolean,
        Null,
        Array,
        ClassName
    }
    private Object objBox;
    private Type type;
    
    @Override
    public String toString () {
        return "IDL" + type + "(" + objBox + ")";
    }
    
    private IDLValue (Object objBox, Type type) {
        this.objBox = objBox;
        this.type = type;
    }
    
    public Type getType () {
        return type;
    }

    public boolean isInteger () {
        return type == Type.Integer;
    }

    public boolean isFloat () {
        return type == Type.Float;
    }
    
    public boolean isString () {
        return type == Type.String;
    }
    
    public boolean isClassName () {
        return type == Type.ClassName;
    }
    
    public boolean isDict () {
        return type == Type.Dict;
    }
    
    public boolean isBoolean () {
        return type == Type.Boolean;
    }

    public boolean isNumber () {
        return isInteger() || isFloat();
    }
    
    
    public boolean isNull () {
        return type == Type.Null;
    }
    
    public boolean isArray () {
        return type == Type.Array;
    }
    
    public long asInteger () {
        return (Long)objBox;
    }
    
    public float asFloat () {
        if (isFloat())
            return (Float)objBox;
        else
            return (float)((Long)objBox);
    }
    
    public String asString () {
        return (String)objBox;
    }
    
    public String asClassName () {
        return (String)objBox;
    }
    
    public IDLValue [] asArray () {
        return (IDLValue [])objBox;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, IDLValue> asDict () {
        return (Map<String, IDLValue>)objBox;
    }
    
    public boolean asBoolean() {
        return (Boolean)objBox;
    }
    
    public static IDLValue createInteger (long i) {
        return new IDLValue(i, Type.Integer);
    }
    
    public static IDLValue createFloat (float f) {
        return new IDLValue(f, Type.Float);
    }
    
    public static IDLValue createString (String s) {
        return new IDLValue(s, Type.String);
    }

    public static IDLValue createClassName (String clsName) {
        return new IDLValue(clsName, Type.ClassName);
    }

    public static IDLValue createDict (Map<String, IDLValue> dict) {
        return new IDLValue(dict, Type.Dict);
    }   
    
    public static IDLValue createBoolean (boolean b) {
        return new IDLValue (b, Type.Boolean);
    }
    
    public static IDLValue createNull () {
        return new IDLValue(null, Type.Null);
    }
    
    public static IDLValue createArray (IDLValue [] array) {
        return new IDLValue(array, Type.Array);
    }
}