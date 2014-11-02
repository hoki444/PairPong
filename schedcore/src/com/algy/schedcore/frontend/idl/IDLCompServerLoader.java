package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseCompServer;

public class IDLCompServerLoader {
    public final String assetName;
    public IDLCompServerCreator creator;
    public IDLCompServerModifier modifier;

    public IDLCompServerLoader(String assetName, IDLCompServerCreator creator,
            IDLCompServerModifier modifier) {
        this.assetName = assetName;
        this.creator = creator;
        this.modifier = modifier;
    }

    public BaseCompServer load (Map<String, IDLValue> dict) {
        return creator.create(dict);
    }
    
    public BaseCompServer modify (BaseCompServer compServer, Map<String, IDLValue> dict) {
        modifier.modify(compServer, dict);
        return compServer;
    }
    public Class<? extends BaseCompServer> getModifiedCompServerType ( ) {
        return modifier.getType();
    }
}
