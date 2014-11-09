package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.util.ObjectDirectory;

public class IDLResult {
    public ObjectDirectory<GameItem> newItemDef;
    public ArrayList<BaseCompServer> createdServers;
    public ArrayList<BaseCompServer> modifiedServers;
    public ArrayList<GameItem> createdItems;
    public ArrayList<GameItem> modifiedItems;
    public AssetList requiredAssetList;
}