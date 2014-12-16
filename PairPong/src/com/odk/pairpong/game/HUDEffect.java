package com.odk.pairpong.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class HUDEffect {
    public abstract void advance(float dt);
    public abstract void render (SpriteBatch spriteBatch);
}