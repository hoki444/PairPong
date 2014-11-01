package com.algy.schedcore.frontend;

import com.algy.schedcore.SchedcoreRuntimeError;
import com.algy.schedcore.frontend.Scene.SceneState;
import com.algy.schedcore.middleend.asset.Eden;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.bullet.Bullet;

public class SceneMgr extends ApplicationAdapter {
    private Scene currentScene = null;
    private Scene nextScene;
    private boolean endScene = false;
    public Eden eden = new Eden();
    public AssetManager assetManager = new AssetManager();
    
    static boolean bulletInitialized = false;
    public static SceneMgr instance = null;
    
    static void initBullet () {
        Bullet.init();
        bulletInitialized = true;
    }

    public SceneMgr (Scene firstScene) {
        startScene (firstScene);
        
        synchronized (SceneMgr.class) {
            if (instance == null) {
                instance = this;
            } else {
                throw new SchedcoreRuntimeError("Scene manager cannot be created twice");
            }
        }

    }
    public final Scene currentScene () {
        return currentScene;
    }

    private void startScene (Scene startScene) {
        startScene.setManager(this);
        this.currentScene = startScene;
    }
    
    private void changeScene (Scene nextScene) {
        this.nextScene = nextScene;
        this.endScene = true;
    }
    
    public static void switchScene (Scene nextScene) {
        instance.changeScene(nextScene);
    }
    
    public void create() {
    }

    public void dispose() {
    }

    public final void pause() {
        if (currentScene != null)
            currentScene.pause();
    }

    public final void render() {
        if (currentScene == null)
            return;

        switch (currentScene.getSceneState()) {
        case Preparing:
            if (currentScene.first) {
                currentScene.internalPreparation ();
                currentScene.firstPreparation ();
                currentScene.first = false;
            }
            currentScene.prepare();
            break;
        case Running:
            if (!endScene) {
                currentScene.internalPreRender();
                currentScene.render();
                currentScene.postRender();
            } else {
                currentScene.Done();
                endScene = false;
            }
            break;
        case TearingDown:
            currentScene.tearDown();
            break;
        case ChangingScene:
            currentScene.destroy();
            break;
        }
        currentScene.syncSceneState();
        if (currentScene.getSceneState() == SceneState.ChangingScene) {
            if (nextScene != null)
                nextScene.setManager(this);
            currentScene = nextScene;
        }
    }

    public final void resize(int width, int height) {
        if (currentScene != null)
            currentScene.resize(width, height);
    }

    public final void resume() {
        if (currentScene != null)
            currentScene.resume();
    }
}