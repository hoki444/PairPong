package com.odk.pctest;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.odk.pairpong.game.NullReceiverFunction;
import com.odk.pairpong.game.NullSenderFunction;
import com.odk.pairpong.game.GameScene;

public class PCMain {
	public static void main (String [] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new SceneMgr(new GameScene(new NullReceiverFunction(), new NullSenderFunction())), config);		
        
	}
}
