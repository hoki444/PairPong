package com.odk.pairpong;

import android.app.Activity;
import android.os.Bundle;

import com.algy.schedcore.frontend.SceneMgr;
import com.algy.schedcore.frontend.TestScene;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class PairPongBoardActivity extends AndroidApplication {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(new SceneMgr(new TestScene()));
    }

    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    }
}
