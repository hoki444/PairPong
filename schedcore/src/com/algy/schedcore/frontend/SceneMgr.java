package com.algy.schedcore.frontend;

import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.asset.LazyAssetManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class SceneMgr extends ApplicationAdapter {
    private Scene currentScene = null;
    Scene nextScene;
    private boolean endScene = false;
    public LazyAssetManager assetManager = new LazyAssetManager();
    public Eden eden = new Eden();
    
    static boolean bulletInitialized = false;
    private static SceneMgr instance = null;
    
    public SceneMgr (Scene firstScene) {
        synchronized (SceneMgr.class) {
            instance = this;
            /*
            if (instance == null) {
                instance = this;
            } else {
                throw new SchedcoreRuntimeError("Scene manager cannot be created twice");
            }
            */
        }
        startScene (firstScene);
    }
    public final Scene currentScene () {
        return currentScene;
    }

    private void startScene (Scene startScene) {
        if (startScene != null)
            startScene.setManager(this);
        this.currentScene = startScene;
    }
    
    private void changeScene (Scene nextScene) {
        this.nextScene = nextScene;
        this.endScene = true;
    }
    
    public static void switchScene (Scene nextScene) {
        synchronized (SceneMgr.class) {
            if (instance != null)
                instance.changeScene(nextScene);
        }
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

        
        
        boolean sceneReplacable = false;
        if (currentScene != null) {
            if (!currentScene.advance(endScene)) {
                sceneReplacable = true;
            }
        } else {
            sceneReplacable = true;
        }

        if (sceneReplacable) {
            if (nextScene != null) {
                synchronized (SceneMgr.class) {
                    if (nextScene != null) {
                        endScene = false;
                        nextScene.setManager(this);
                        currentScene = nextScene;
                    }
                }
            }
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