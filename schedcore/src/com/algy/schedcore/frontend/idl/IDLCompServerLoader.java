package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompServer;

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

    public BaseCompServer make (IDLGameContext context, Map<String, IDLValue> dict) {
        return creator.create(context, dict);
    }
    
    public BaseCompServer modify (IDLGameContext context, BaseCompServer compServer, Map<String, IDLValue> dict) {
        modifier.modify(context, compServer, dict);
        return compServer;
    }
    public Class<? extends BaseCompServer> getModifiedCompServerType ( ) {
        return modifier.getType();
    }
}
