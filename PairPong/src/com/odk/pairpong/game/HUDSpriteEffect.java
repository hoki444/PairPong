package com.odk.pairpong.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class HUDSpriteEffect extends HUDEffect {
    public static interface Interpolater {
        public float interpolate(float t);
    }
    
    private Interpolater intpt = new Interpolater() {public float interpolate(float t) { return t; }};
    public float t;
    public float duration;
    public Vector2 initialPos;
    public Vector2 finalPos;
    public float initialScale = 1f;
    public float finalScale = 1f;
    public float initialRotation = 0;
    public float finalRotation = 0;
    public float initialAlpha = 1f;
    public float finalAlpha = 1f;
    private TextureRegion texRegion;
    
    public HUDSpriteEffect (TextureRegion texRegion, float duration, Vector2 initialPos, Vector2 finalPos) {
        this.texRegion = texRegion;
        this.duration = duration;
        this.initialPos = initialPos;
        this.finalPos = finalPos;
        this.t = duration;
    }
    
    public HUDSpriteEffect (TextureRegion texRegion, float duration, Vector2 position) {
        this(texRegion, duration, position, position);
    }
    
    public HUDSpriteEffect setInterpolater (Interpolater intpt) {
        this.intpt = intpt;
        return this;
    }
    
    public HUDSpriteEffect setScale (float initialScale, float finalScale) {
        this.initialScale = initialScale;
        this.finalScale = finalScale;
        return this;
    }

    public HUDSpriteEffect setPosition (Vector2 initialPos, Vector2 finalPos) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
        return this;
    }

    public HUDSpriteEffect setRotation (float initialRotation, float finalRotation) {
        this.initialRotation = initialRotation;
        this.finalRotation = finalRotation;
        return this;
    }

    
    public HUDSpriteEffect setAlpha (float initialAlpha, float finalAlpha) {
        this.initialAlpha = initialAlpha;
        this.finalAlpha = finalAlpha;
        return this;
    }
    
    public void rewind() {
        t = 0;
    }
    
    public boolean isRunning() {
        return (this.t < this.duration);
    }
    

    Affine2 affine = new Affine2();
    Vector2 pos = new Vector2();
    float rotation = 0;
    float scale = 1.f;
    float alpha = 1.f;
    
    private void getState () {
        float k = intpt.interpolate(t);
        float width = texRegion.getRegionWidth();
        float height = texRegion.getRegionHeight();

        pos.set(finalPos).sub(initialPos).scl(t).add(initialPos);
        rotation = (finalRotation - initialRotation) * k + initialRotation;
        scale = (finalScale - initialScale) *k + initialScale;
        alpha = (finalAlpha - initialAlpha) *k + initialAlpha;
        


        affine.setToTrnRotScl(new Vector2(pos).sub(-width * scale /2, -height * scale /2), rotation, new Vector2(scale, scale));
    }

    @Override
    public void advance(float dt) {
        if (t < duration) {
            t += dt;
            getState();
        } else {
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (isRunning()) {
            if (t == 0)
                getState();
            float width = texRegion.getRegionWidth();
            float height = texRegion.getRegionHeight();
            Color oldColor;
            oldColor = spriteBatch.getColor();
            spriteBatch.setColor(1, 1, 1, alpha);
            spriteBatch.draw(texRegion, width, height, affine);
            spriteBatch.setColor(oldColor);
        }
    }
}