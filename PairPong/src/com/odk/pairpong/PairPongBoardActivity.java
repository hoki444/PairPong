package com.odk.pairpong;


import android.os.Bundle;
import android.view.WindowManager;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.game.TestScene;

public class PairPongBoardActivity extends AndroidApplication {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialize(new SceneMgr(new TestScene(new QPairReceiverFunction(), new QPairSenderFunction(this))));
    }

    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    }
}
