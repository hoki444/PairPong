package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.KeyError;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.util.MutableLister;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Eden extends BaseCompServer {
    private AssetDirectory <GameItem> directory = new AssetDirectory<GameItem>();
    
    public GameItem getPrototype (String assetRawName) {
        if (!hasPrototype(assetRawName))
            throw new KeyError(assetRawName);
        return directory.get(assetRawName);
    }
    
    public boolean hasPrototype (String assetRawName) {
        return directory.has(assetRawName);
    }
    
    public void putPrototype(String assetRawName, GameItem prototype) {
        directory.put(assetRawName, prototype, true);
    }
    
    public GameItem remove(String assetRawName) {
        return directory.remove(assetRawName);
    }
    
    /*
     * make methods
     */
    public GameItem make (String assetRawName) {
        return make (assetRawName, (Matrix4)null);
    }
    
    public GameItem make (String assetRawName, Vector3 pos) {
        return make (assetRawName, pos, new Quaternion());
    }

    public GameItem make (String assetRawName, Vector3 pos, Quaternion ori) {
        return make (assetRawName, pos, ori, new Vector3(1,1,1));
    }

    public GameItem make (String assetRawName, Vector3 pos, Quaternion ori, Vector3 scale) {
        return make (assetRawName, new Matrix4(pos, ori, scale));
    }
    
    public GameItem make (String assetRawName, Matrix4 mat) {
        GameItem proto = getPrototype(assetRawName);
        return proto.duplicate(mat);
    }
    
    public GameItem make (String assetRawName, Matrix4 mat, IItemModifier modifier) {
        GameItem gameItem = make(assetRawName, mat);
        
        for (BaseComp comp : gameItem) {
            if (modifier.modifiable(comp.getClass())) {
                modifier.compModifier(comp.getClass()).modify(comp);
            }
        }
        return gameItem;
    }

    @Override
    public void hookFilters(MutableLister<Class<? extends BaseComp>> compSigList) {
    }

    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }
}