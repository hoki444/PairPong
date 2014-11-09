package com.algy.schedcore.frontend.idl.reflection;

import java.util.HashMap;


public class DefaultValue extends MetaReflectionField {
    public HashMap<String, Object> defvals = new HashMap<String, Object>();
    public DefaultValue (Object ...keyvalue) {
        /*
         * e.g)
         * 
         * DefaultValue ("key1", 42, "key2", "Veritas")
         * => 42 for default value of "key1" field and "Veritas" for default value of "key2"
         * 
         * Primitive types are not supported.
         */
        if (keyvalue.length % 2 != 0) {
            throw new IllegalArgumentException("The number of arguments should be even number such that they form string key-value pairs.");
        }

        for (int idx = 0; idx < keyvalue.length / 2 ; idx += 2) {
            String key = (String) keyvalue[idx];
            Object val = keyvalue[idx + 1];
            defvals.put(key, val);
        }
    }
}