package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class SimpleCameraControllerComp extends InputComp {
    private CameraInputController controller;
    
    @Override
    public BaseComp duplicate() {
        return new SimpleCameraControllerComp();
    }

    @Override
    public void onItemAdded () {
        controller = new CameraInputController(getCompManager(CameraServer.class).getCamera());
    }

    public void cancel() {
        this.controller.cancel();
    }

    public boolean equals(Object obj) {
        return this.controller.equals(obj);
    }

    public int hashCode() {
        return this.controller.hashCode();
    }

    public boolean keyTyped(char character) {
        return this.controller.keyTyped(character);
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return this.controller.mouseMoved(screenX, screenY);
    }

    public boolean touchDown(float x, float y, int pointer, int button) {
        return this.controller.touchDown(x, y, pointer, button);
    }

    public boolean touchDragged(float x, float y, int pointer) {
        return this.controller.touchDragged(x, y, pointer);
    }

    public void update() {
        this.controller.update();
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return this.controller.touchDown(screenX, screenY, pointer, button);
    }

    public boolean touchUp(float x, float y, int pointer, int button) {
        return this.controller.touchUp(x, y, pointer, button);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return this.controller.touchUp(screenX, screenY, pointer, button);
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return this.controller.touchDragged(screenX, screenY, pointer);
    }

    public boolean scrolled(int amount) {
        return this.controller.scrolled(amount);
    }

    public boolean zoom(float amount) {
        return this.controller.zoom(amount);
    }

    public boolean isLongPressed() {
        return this.controller.isLongPressed();
    }

    public boolean keyDown(int keycode) {
        return this.controller.keyDown(keycode);
    }

    public boolean isLongPressed(float duration) {
        return this.controller.isLongPressed(duration);
    }

    public boolean isPanning() {
        return this.controller.isPanning();
    }

    public boolean keyUp(int keycode) {
        return this.controller.keyUp(keycode);
    }

    public void reset() {
        this.controller.reset();
    }

    public void invalidateTapSquare() {
        this.controller.invalidateTapSquare();
    }

    public void setTapSquareSize(float halfTapSquareSize) {
        this.controller.setTapSquareSize(halfTapSquareSize);
    }

    public void setTapCountInterval(float tapCountInterval) {
        this.controller.setTapCountInterval(tapCountInterval);
    }

    public void setLongPressSeconds(float longPressSeconds) {
        this.controller.setLongPressSeconds(longPressSeconds);
    }

    public void setMaxFlingDelay(long maxFlingDelay) {
        this.controller.setMaxFlingDelay(maxFlingDelay);
    }

    public String toString() {
        return this.controller.toString();
    }
}
