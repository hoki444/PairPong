package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;
import java.util.Map;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.Item;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.asset.Eden;
import com.algy.schedcore.util.Pair;

class IDLParserForScene extends IDLParser {
    private Eden eden;
    private ArrayList<GameItem> sceneItems = new ArrayList<GameItem>();
    private Item<BaseCompServer, Object> serverItem;
    public IDLParserForScene(String source, Eden eden) {
        super(source);
        this.eden = eden;
        this.serverItem = new Item<BaseCompServer, Object>(BaseCompServer.class);
    }
    
    public IDLParserForScene(String source, Eden eden, Item<BaseCompServer, Object> serverItem) {
        super(source);
        this.eden = eden;
        this.serverItem = serverItem;
    }

    @Override
    protected void actionUseServer(String assetName,
            Map<String, IDLValue> creationDict,
            Map<String, IDLValue> modificationDict) {
        IDLCompServerLoader loader = IDLLoader.assertGetCompServerLoader(assetName);
        if (creationDict != null)
            serverItem.add(loader.load(creationDict));
        if (modificationDict != null) {
            loader.modify(serverItem.as(loader.getModifiedCompServerType()), modificationDict);
        }
    }

    @Override
    protected void actionUseItem(String assetName, ArrayList<CompDescriptor> modificationList) {
        GameItem proto = eden.make(assetName);
        if (proto == null) {
            throw new IDLNameError(assetName + " is not found " + getCurrentScannerLoc());
        }
        for (CompDescriptor desc : modificationList) {
            IDLCompLoader loader = IDLLoader.assetGetCompLoader(desc.compName);
            loader.modify(proto.as(loader.getModifiedCompType()), desc.dict);
        }
        sceneItems.add(proto);
    }

    @Override
    protected void actionCreateItem(ArrayList<CompDescriptor> creationList) {
        GameItem gameItem = IDLLoader.fromDescList(this, creationList);
        sceneItems.add(gameItem);
    }

    public Pair<ArrayList<GameItem>, Item<BaseCompServer, Object>> getResult () {
        return Pair.cons(sceneItems, serverItem);
            
    }
}