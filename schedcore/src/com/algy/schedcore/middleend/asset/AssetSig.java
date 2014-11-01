package com.algy.schedcore.middleend.asset;

public class AssetSig {
    public AssetName assetName;
    public Class<?> assetClass;
    
    public AssetSig (String assetRawName, Class<?> assetClass) {
        this.assetName = AssetName.parse(assetRawName);
        this.assetClass = assetClass;
    }
}