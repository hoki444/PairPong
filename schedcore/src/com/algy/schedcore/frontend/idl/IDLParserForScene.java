package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.asset.AssetList;

class IDLParserForScene extends IDLParser {
    private IDLGameContext context;
    private ArrayList<GameItem> sceneItems = new ArrayList<GameItem>();
    private TreeSet<String> addedItemName = new TreeSet<String>();
    private ArrayList<BaseCompServer> sceneServers = new ArrayList<BaseCompServer>();
    private HashSet<BaseCompServer> modifiedServers = new HashSet<BaseCompServer>();

    public IDLParserForScene(String source, IDLGameContext context) {
        super(source);
        this.context = context;
    }

    public static GameItem fromDescList (IDLParser parser, 
                                         IDLGameContext context,
                                         ArrayList<CompDescriptor> creationList) {
        GameItem gameItem = new GameItem();

        for (CompDescriptor desc : creationList) {
            IDLCompLoader loader = IDLLoader.assetGetCompLoader(desc.compName);
            BaseComp comp = loader.load(context, desc.dict);
            if (comp instanceof Transform) {
                gameItem.remove(Transform.class);
                gameItem.add(comp);
            } else {
                gameItem.add(comp);
            }
        }
        return gameItem;
    }

    @Override
    protected void actionUseServer(String slashName,
            Map<String, IDLValue> creationDict,
            Map<String, IDLValue> modificationDict) {
        IDLCompServerLoader loader = IDLLoader.assertGetCompServerLoader(slashName);
        BaseCompServer server = null;
        if (creationDict != null) {
            server = loader.make(context, creationDict);
        } else {
            server = context.core().server(loader.getModifiedCompServerType());
        }

        if (modificationDict != null) {
            if (server != null) {
                loader.modify(context, server, modificationDict);
            }
        }

        if (creationDict != null) {
            sceneServers.add(server);
        } else if (modificationDict != null) {
            modifiedServers.add(server);
        }
    }

    @Override
    protected void actionUseItem(String slashName, String itemName, ArrayList<CompDescriptor> modificationList) {
        GameItem gameItem = context.eden().make(slashName);
        if (gameItem == null) {
            throw new IDLNameError(slashName + " is not found " + getCurrentScannerLoc());
        }
        if (itemName != null) {
            if (addedItemName.contains(itemName)) {
                throw new IDLLoadError("Duplicated name of game item '" + itemName + "'");
            }
            addedItemName.add(itemName);
        }
        for (CompDescriptor desc : modificationList) {
            IDLCompLoader loader = IDLLoader.assetGetCompLoader(desc.compName);
            loader.modify(context, gameItem.as(loader.getModifiedCompType()), desc.dict);
        }
        gameItem.setName(itemName);
        sceneItems.add(gameItem);
    }

    @Override
    protected void actionCreateItem(String itemName, ArrayList<CompDescriptor> creationList) {
        GameItem gameItem = fromDescList(this, context, creationList);
        if (itemName != null) {
            if (addedItemName.contains(itemName)) {
                throw new IDLLoadError("Duplicated name of game item '" + itemName + "'");
            }
            addedItemName.add(itemName);
        }
        gameItem.setName(itemName);
        sceneItems.add(gameItem);
    }
    
    public IDLResult getResult () {
        IDLResult result = new IDLResult();
        result.createdItems = sceneItems;
        result.createdServers = sceneServers;
        result.modifiedServers = new ArrayList<BaseCompServer>(modifiedServers);
        AssetList assetList = new AssetList();
        for (GameItem item : sceneItems) {
            item.getUsedAsset(assetList);
        }
        result.requiredAssetList = assetList;
        return result;
    }
}