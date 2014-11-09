package com.algy.schedcore.middleend;

import java.util.Map.Entry;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.KeyError;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.util.Lister;
import com.algy.schedcore.util.ObjectDirectory;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Eden extends BaseCompServer {
    private ObjectDirectory <GameItem> directory = new ObjectDirectory<GameItem>();

    public Eden () {
    }
    
    public void update (ObjectDirectory<GameItem> defs) {
        for (Entry<String, GameItem> entry : defs.entries()) {
            this.putPrototype(entry.getKey(), entry.getValue());
        }
    }
    
    public GameItem getPrototype (String prototypeName) {
        if (!hasPrototype(prototypeName))
            throw new KeyError(prototypeName);
        return directory.get(prototypeName);
    }
    
    public boolean hasPrototype (String prototypeName) {
        return directory.has(prototypeName);
    }
    
    public void putPrototype(String prototypeName, GameItem prototype) {
        directory.put(prototypeName, prototype, true);
    }
    
    
    public GameItem remove(String prototypeName) {
        return directory.remove(prototypeName);
    }
    
    public Iterable<Entry<String, GameItem>> entries () {
        return directory.entries();
    }

    public void getUsedAsset (String prototypeName, AssetList assetListOut) {
        GameItem proto = getPrototype(prototypeName);
        proto.getUsedAsset(assetListOut);
    }

    /*
     * make methods (In=game Method)
     */
    public GameItem make (String prototypeName) {
        return make (prototypeName, (Matrix4)null);
    }
    
    public GameItem make (String prototypeName, Vector3 pos) {
        return make (prototypeName, pos, new Quaternion());
    }

    public GameItem make (String prototypeName, Vector3 pos, Quaternion ori) {
        return make (prototypeName, pos, ori, new Vector3(1,1,1));
    }

    public GameItem make (String prototypeName, Vector3 pos, Quaternion ori, Vector3 scale) {
        return make (prototypeName, new Matrix4(pos, ori, scale));
    }
    
    public GameItem make (String prototypeName, Matrix4 mat) {
        GameItem proto = getPrototype(prototypeName);
        if (proto == null)
            return null;
        return proto.duplicate(mat);
    }
    
    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> compSigList) {
    }

    @Override
    public void hookAddComp(BaseComp comp) {
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
    }
}