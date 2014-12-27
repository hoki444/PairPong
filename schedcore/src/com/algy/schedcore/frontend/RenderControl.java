package com.algy.schedcore.frontend;

import java.util.concurrent.Semaphore;

class RenderControl {
    private final Scene scene;
    /**
     * @param scene
     */
    RenderControl(Scene scene) {
        this.scene = scene;
    }

    private Semaphore startSem = new Semaphore(0);
    private Semaphore endSem = new Semaphore(0);
    public void renderScene () {
        try {
            startSem.acquire();
            this.scene.clearBuffers();
            this.scene.render();
            this.scene.postRender();
            endSem.release();
        } catch (InterruptedException e) {
        }
    }

    public void requestAndWaitForRender() {
        try {
            startSem.release();
            endSem.acquire();
        } catch (InterruptedException e) {
        }
    }
}