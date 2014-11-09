package com.algy.schedcore.middleend.asset;

import com.badlogic.gdx.graphics.g3d.Model;

public interface ModelFactory extends AssetUsable {
    public Model make (AssetProvider assetProvider);
}