package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompServer;

public interface IDLCompServerCreator {
    public BaseCompServer create (IDLGameContext context, Map<String, IDLValue> dict);
}