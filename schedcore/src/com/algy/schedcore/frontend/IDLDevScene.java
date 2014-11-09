package com.algy.schedcore.frontend;

import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class IDLDevScene extends Scene {
    private BitmapFont bitmapFont;
    @Override
    public void postRender() {
    }

    @Override
    public void firstPreparation() {
        bitmapFont = new BitmapFont();
    }

    @Override
    public void prepare() {
        Done ();
    }

    @Override
    public void tearDown() {
        bitmapFont.dispose();
        Done ();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
