package com.odk.pairpong;


import android.os.Bundle;
import android.view.WindowManager;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.game.MainScene;
import com.odk.pairpong.game.GameScene;

public class PairPongBoardActivity extends AndroidApplication {
	QPairSenderFunction sfunction;
	public static boolean isstarting=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sfunction = new QPairSenderFunction(this);
		sfunction.setpackage("com.odk.pairpongsender");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialize(new SceneMgr(new MainScene(new QPairReceiverFunction(), sfunction, new PhoneServiceFunction())));
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	System.exit(0);
    }
}
