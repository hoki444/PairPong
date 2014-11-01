package com.algy.schedcore.frontend;

import com.badlogic.gdx.graphics.Color;

public class SceneConfig {
    public int itemRenderingPeriod = 20;
    public boolean perspectiveCamera = true;
    public float fieldOfView = 67f;

    public boolean useBulletPhysics = true;
    
    public Color backgroundColor = new Color(.1f, .1f, .1f, 1f);
    
    public static SceneConfig defaultConfig () {
        return new SceneConfig();
    }
}