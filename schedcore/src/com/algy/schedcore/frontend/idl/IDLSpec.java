package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.middleend.asset.AssetDirectory;

public class IDLSpec {
    private AssetDirectory<IDLCompCreator> compCreators;
    private AssetDirectory<IDLCompModifier> compModifiers;
    private AssetDirectory<IDLCompServerCreator> compServerCreators;
}