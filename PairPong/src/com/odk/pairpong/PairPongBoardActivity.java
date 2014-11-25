package com.odk.pairpong;


import android.os.Bundle;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.game.TestScene;

public class PairPongBoardActivity extends AndroidApplication {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(new SceneMgr(new TestScene(new QPairReceiverFunction())));
    }

    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    }
}
