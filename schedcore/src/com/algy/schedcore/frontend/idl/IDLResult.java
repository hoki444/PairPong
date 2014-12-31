package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.GameItem;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.util.ObjectDirectory;

public class IDLResult {
    public ObjectDirectory<GameItem> newItemDef;
    public ArrayList<BaseCompMgr> createdServers;
    public ArrayList<BaseCompMgr> modifiedServers;
    public ArrayList<GameItem> createdItems;
    public ArrayList<GameItem> modifiedItems;
    public AssetList requiredAssetList;
}