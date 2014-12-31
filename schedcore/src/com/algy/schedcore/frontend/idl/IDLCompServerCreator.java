package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompMgr;

public interface IDLCompServerCreator {
    public BaseCompMgr create (IDLGameContext context, Map<String, IDLValue> dict);
}