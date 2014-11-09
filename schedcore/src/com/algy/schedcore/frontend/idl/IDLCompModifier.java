package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseComp;

public interface IDLCompModifier {
    public Class<? extends BaseComp> getType();
    public void modify (IDLGameContext context, BaseComp freshComp, Map<String, IDLValue> dict);
}