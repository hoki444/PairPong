package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompServer;

public interface IDLCompServerCreator {
    public BaseCompServer create (Map<String, IDLValue> dict);
}