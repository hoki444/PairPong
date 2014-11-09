package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;

import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.util.ObjectDirectory;

class IDLParserForDef extends IDLParser {
    private IDLGameContext context;
    public IDLParserForDef(String source, IDLGameContext context) {
        super(source);
        this.context = context;
    }
    private ObjectDirectory<GameItem> resultDir = new ObjectDirectory<GameItem>();

    @Override
    protected void actionDefItem(String slashName, ArrayList<CompDescriptor> creationList) {
        if (resultDir.has(slashName))
            throw new IDLNameError("duplicated name of item (" + slashName + ") " + 
                                   getCurrentScannerLoc());
        GameItem gameItem = IDLParserForScene.fromDescList(this, context, creationList);
        resultDir.put(slashName, gameItem, true);
    }
    
    public IDLResult getResult () {
        IDLResult result = new IDLResult();
        result.newItemDef = resultDir;
        return result;
    }
}