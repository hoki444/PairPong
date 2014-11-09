package com.algy.schedcore.middleend.asset;

public interface AssetProvider {
    public <T> T get(String assetName, Class<T> type);
}
