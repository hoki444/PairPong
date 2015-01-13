package com.algy.schedcore;

import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.AssetUsable;
import com.algy.schedcore.util.Item;
import com.algy.schedcore.util.LinkedListCell;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GameItem extends Item<BaseComp, GameItemSpace> implements LinkedListCell {
    private LinkedListCell prev = null, next = null;
    private BaseItemType itemType = new DefaultItemType();
    

    public GameItem() {
        super(BaseComp.class);
        this.add(new Transform(0, 0, 0)); 
    }
    
    public GameItem (BaseComp ... components) {
        super(BaseComp.class);
        boolean transformProvided = false;
        
        for (BaseComp comp : components) {
            if (comp instanceof Transform) {
                transformProvided = true;
            } 
            add(comp);
        }

        if (!transformProvided) {
            add(new Transform(0, 0, 0));
        }
    }
    
    private GameItem(Transform transform) {
        super(BaseComp.class);
        this.add(transform);
    }

    public BaseItemType getItemType () {
        return itemType;
    }
    
    public void setItemType (BaseItemType newType) {
        GameItemSpace itemSpace = getGameItemSpace();
        BaseItemType oldType = this.getItemType();
        oldType.attachTo(null);

        this.itemType = newType;
        if (itemSpace != null) {
            itemSpace.updateTypeMap(this, oldType);
        }
        newType.attachTo(this);
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
                BaseComp copiedComp = comp.duplicate();
                if (copiedComp != null)
                    newItem.add(copiedComp);
            }
        }
        newItem.setItemType(getItemType().duplicate());

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
    
    protected GameItemSpace getGameItemSpace () {
        return owner();
    }
    
    protected <T extends BaseCompMgr> T getCompManager(Class<T> serverClass) {
        return owner().getCompMgrSpace().as(serverClass);
    }
    
    
    protected AssetServer assetServer() {
        return getCompManager(AssetServer.class);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        removeAll();
    }

    @Override
    public LinkedListCell getNext() {
        return next;
    }

    @Override
    public LinkedListCell getPrev() {
        return prev;
    }

    @Override
    public void setPrev(LinkedListCell cell) {
        this.prev = cell;
    }

    @Override
    public void setNext(LinkedListCell cell) {
        this.next = cell;
    }
    
}
