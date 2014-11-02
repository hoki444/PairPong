package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;

import com.algy.schedcore.frontend.idl.IDLParser.CompDescriptor;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.asset.Eden;

class IDLParserForDef extends IDLParser {
    public IDLParserForDef(String source) {
        super(source);
    }
    private Eden result = new Eden();

    @Override
    protected void actionDefItem(String assetName, ArrayList<CompDescriptor> creationList) {
        if (result.hasPrototype(assetName))
            throw new IDLNameError("duplicated name of item (" + assetName + ") " + 
                                   getCurrentScannerLoc());
        GameItem gameItem = IDLLoader.fromDescList(this, creationList);
        result.putPrototype(assetName, gameItem);
    }
    
    public Eden getResult () {
        return result;
    }
}