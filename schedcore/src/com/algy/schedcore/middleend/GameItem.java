package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.ICore;
import com.algy.schedcore.Item;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.AssetUsable;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GameItem extends Item<BaseComp, ICore> {
    GameItem prev = null, next = null;
    private String name = null;

    public GameItem() {
        super(BaseComp.class);
        this.add(new Transform(0, 0, 0)); 
    }
    
    private GameItem(Transform transform) {
        super(BaseComp.class);
        this.add(transform);
    }

    public String getName () {
        return name;
    }
    
    public void setName (String newName) {
        GameCore core = core();
        String oldName = name;
        this.name = newName;
        if (core != null) {
            core.updateNameMap(this, oldName);
        }
    }
    
    public void getUsedAsset (AssetList assetListOut) {
        for (BaseComp comp : this) {
            if (comp instanceof AssetUsable) {
                ((AssetUsable)comp).declareAsset(assetListOut);
            }
        }
    }
    
    public GameItem duplicate (Matrix4 trMat) {
        GameItem newItem;
        if (trMat == null) {
            newItem = new GameItem ();
            newItem.getTransform().modify().set(this.getTransform().get());
        } else {
            newItem = new GameItem(new Transform(trMat));
        }
        for (BaseComp comp : this) {
            if (!(comp instanceof Transform)) {
                BaseComp copiedComp = (BaseComp)comp.duplicate();
                if (copiedComp != null)
                    newItem.add(copiedComp);
            }
        }
        return newItem;
    }

    public GameItem duplicate (Vector3 pos, Quaternion ori, Vector3 scale) {
        return duplicate(new Matrix4(pos, ori, scale));
    }
    
    public GameItem duplicate (Vector3 pos, Quaternion ori) {
        return duplicate (pos, ori, new Vector3(1, 1, 1));
    }

    public GameItem duplicate (Vector3 pos) {
        return duplicate (pos, new Quaternion());
    }
    
    public GameItem duplicate () {
        return duplicate((Matrix4)null);

    }
    public Transform getTransform() {
        return this.as(Transform.class);
    }
    
    protected GameCore core () {
        return (GameCore)owner();
    }
    
    protected <T extends BaseCompServer> T server(Class<T> serverClass) {
        return core().server(serverClass);
    }
    
    protected AssetServer assetServer() {
        return server(AssetServer.class);
    }
}
