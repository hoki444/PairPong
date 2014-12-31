package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompMgr;

public interface IDLCompServerModifier {
    public Class<? extends BaseCompMgr> getType ();
    public void modify (IDLGameContext context, BaseCompMgr compServer, Map<String, IDLValue> dict);
}
