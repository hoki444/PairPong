package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseComp;

public interface IDLCompCreator {
    /*
     * key value model
     * 
     */
    public BaseComp create (IDLGameContext context, Map<String, IDLValue> dict);
}