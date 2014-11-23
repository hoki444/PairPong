package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.frontend.SceneMgr;
import com.algy.schedcore.frontend.idl.IDLTestScene;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class Launcher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new SceneMgr(new IDLTestScene()), config);
    }
}