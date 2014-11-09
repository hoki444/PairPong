package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseComp;

public class IDLCompLoader {
    public final String assetName;
    public IDLCompCreator creator;
    public IDLCompModifier modifier;
    
    
    public IDLCompLoader(String assetName, IDLCompCreator creator, IDLCompModifier modifier) {
        super();
        this.assetName = assetName;
        this.creator = creator;
        this.modifier = modifier;
    }

    public BaseComp load (IDLGameContext context, Map<String, IDLValue> dict) {
        return creator.create(context, dict);
    }
    
    public void modify (IDLGameContext context, BaseComp comp, Map<String, IDLValue> dict) {
        modifier.modify(context, comp, dict);
    }
    
    public Class<? extends BaseComp> getModifiedCompType ( ) {
        return modifier.getType();
    }
}
