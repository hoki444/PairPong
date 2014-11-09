package com.algy.schedcore.frontend.idl.reflection.templates;

import java.util.HashMap;

import com.algy.schedcore.frontend.idl.reflection.IDLObjectReflectorTemplate;
import com.algy.schedcore.middleend.asset.AssetSig;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetSignatureObjTemplate extends IDLObjectReflectorTemplate {
    public String assetName;
    public String type;
    
    public static HashMap<String, Class<?>> typeNames = new HashMap<String, Class<?>>();
    static {
        registerTypeName("Model", Model.class);
		registerTypeName("BitmapFont", BitmapFont.class);
		registerTypeName("Music", Music.class);
		registerTypeName("Pixmap", Pixmap.class);
        registerTypeName("Sound", Sound.class);
        registerTypeName("TextureAtlas", TextureAtlas.class);
        registerTypeName("Texture", Texture.class);
        registerTypeName("Skin", Skin.class);
        registerTypeName("ParticleEffect", ParticleEffect.class);
        registerTypeName("PolygonRegion", PolygonRegion.class);
    }
    
    public static synchronized void registerTypeName (String typeName, Class<?> type) {
        typeNames.put(typeName, type);
    }
    
    public static synchronized boolean hasTypeName (String typeName) {
        return typeNames.containsKey(typeName);
    }
    @Override
    public synchronized Object make() {
        if (typeNames.containsKey(type)) {
            Class<?> cls;
            cls = typeNames.get(type);
            return new AssetSig(assetName, cls);
        } else {
            unexpectedValueError(typeNames.keySet().toArray(new String[0]), type);
            return null;
        }
    }
}
