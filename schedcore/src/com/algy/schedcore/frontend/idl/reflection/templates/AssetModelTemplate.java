package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.middleend.AssetModelComp;

public class AssetModelTemplate extends IDLCompTemplate {
    static RequiredFields _req = new RequiredFields("assetName");
    String assetName;
    @Override
    protected BaseComp create(IDLGameContext context) {
        return new AssetModelComp(assetName);
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
        if (assetName != null) {
            ((AssetModelComp)comp).assetName = assetName;
        }
    }
}
