package com.algy.schedcore.frontend;

import com.algy.schedcore.SchedcoreRuntimeError;
import com.algy.schedcore.frontend.Scene.SceneState;
import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.asset.LazyAssetManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.bullet.Bullet;

public class SceneMgr extends ApplicationAdapter {
    private Scene currentScene = null;
    private Scene nextScene;
    private boolean endScene = false;
    public LazyAssetManager assetManager = new LazyAssetManager();
    public Eden eden = new Eden();
    
    static boolean bulletInitialized = false;
    public static SceneMgr instance = null;
    
    static void initBullet () {
        Bullet.init();
        bulletInitialized = true;
    }

    public SceneMgr (Scene firstScene) {
        synchronized (SceneMgr.class) {
            if (instance == null) {
                instance = this;
            } else {
                throw new SchedcoreRuntimeError("Scene manager cannot be created twice");
            }
        }
        startScene (firstScene);
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
        synchronized (SceneMgr.class) {
            this.assetManager.dispose();
        	instance = null;
        }
    }

    public final void pause() {
        if (currentScene != null)
            currentScene.pause();
    }

    private boolean requestedToExit = false;
    public final void render() {
        if (currentScene == null) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glClearColor(0, 0, 0, 0);
            if (!requestedToExit) {
                requestedToExit = true;
                Gdx.app.exit();
            }
            return;
        }
        currentScene.clearBuffers();
        switch (currentScene.getSceneState()) {
        case Preparing:
            if (currentScene.first) {
                currentScene.internalPreparation ();
                currentScene.firstPreparation ();
                currentScene.first = false;
            }
            currentScene.prepare();
            break;
        case InitResource:
            currentScene.doInitResource();
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
        default:
            break;
        }
        currentScene.syncSceneState();
        if (currentScene.getSceneState() == SceneState.ChangingScene) {
            currentScene.destroy();
            if (nextScene != null)
                nextScene.setManager(this);
            currentScene = nextScene;
            return;
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