package com.algy.schedcore.middleend.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

public class AssetSig {
    public final String assetName;
    public final Class<?> assetClass;
    
    public AssetSig (String assetName, Class<?> assetClass) {
        this.assetName = assetName;
        this.assetClass = assetClass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.assetName == null) ? 0 : this.assetName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssetSig other = (AssetSig) obj;
        if (this.assetName == null) {
            if (other.assetName != null)
                return false;
        } else if (!this.assetName.equals(other.assetName))
            return false;
        return true;
    }
    
    public AssetSig sigModel (String assetName) {
        return new AssetSig(assetName, Model.class);
    }
    
    public AssetSig sigTexture (String assetName) {
        return new AssetSig(assetName, Texture.class);
    }
}