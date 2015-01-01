package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompMgr;

public class IDLCompServerLoader {
    public final String compServerName;
    public IDLCompServerCreator creator;
    public IDLCompServerModifier modifier;

    public IDLCompServerLoader(
            String compServerName, 
            IDLCompServerCreator creator,
            IDLCompServerModifier modifier) {
        this.compServerName = compServerName;
        this.creator = creator;
        this.modifier = modifier;
    }

    public BaseCompMgr make (IDLGameContext context, Map<String, IDLValue> dict) {
        return creator.create(context, dict);
    }
    
    public BaseCompMgr modify (IDLGameContext context, BaseCompMgr compServer, Map<String, IDLValue> dict) {
        modifier.modify(context, compServer, dict);
        return compServer;
    }
    public Class<? extends BaseCompMgr> getModifiedCompServerType ( ) {
        return modifier.getType();
    }
}
