package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.asset.AssetList;

class IDLParserForMod extends IDLParser {
    private IDLGameContext context;
    private HashSet<GameItem> modifiedItems = new HashSet<GameItem>();
    private HashSet<BaseCompServer> modifiedServers = new HashSet<BaseCompServer>();
    public IDLParserForMod(String source, IDLGameContext context) {
        super(source);
        this.context = context;
    }

    @Override
    protected void actionModifyItem(String itemName,
            ArrayList<CompDescriptor> modificationList) {
        GameItem gameItem = context.core().getItemWithName(itemName);
        if (gameItem == null) {
            throw new IDLLoadError("gameItem with the name of '" + itemName + "' is not found");
        }
        for (CompDescriptor desc : modificationList) {
            IDLCompLoader loader = IDLLoader.assetGetCompLoader(desc.compName);
            loader.modify(context, 
                          gameItem.as(loader.getModifiedCompType()), 
                          desc.dict);
        }
        modifiedItems.add(gameItem);
    }

    @Override
    protected void actionModifyServer(String slashName,
            Map<String, IDLValue> modificationDict) {
        IDLCompServerLoader loader = IDLLoader.assertGetCompServerLoader(slashName);
        BaseCompServer destServer = context.core().server(loader.getModifiedCompServerType());
        loader.modify(context, destServer, modificationDict);
        modifiedServers.add(destServer);
    }
    public IDLResult getResult () {
        IDLResult result = new IDLResult();
        AssetList assetList = new AssetList();
        result.modifiedItems = new ArrayList<GameItem>(modifiedItems);
        result.modifiedServers = new ArrayList<BaseCompServer>(modifiedServers);
        for (GameItem item : modifiedItems) {
            item.getUsedAsset(assetList);
        }
        result.requiredAssetList = assetList;
        return result;
    }
}