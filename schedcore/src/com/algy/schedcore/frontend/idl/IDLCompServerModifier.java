package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompServer;

public interface IDLCompServerModifier {
    public Class<? extends BaseCompServer> getType ();
    public void modify (IDLGameContext context, BaseCompServer compServer, Map<String, IDLValue> dict);
}
