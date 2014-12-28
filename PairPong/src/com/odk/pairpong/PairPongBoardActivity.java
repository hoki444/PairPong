package com.odk.pairpong;


import android.content.Intent;
import android.os.Bundle;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpong.game.MainScene;
import com.odk.pairpongsender.MainActivity;

public class PairPongBoardActivity extends AndroidApplication {
	private QPairCommFunction commFun;
	private boolean commFunRegistered;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		commFun = new QPairCommFunction("com.odk.pairpong");// new QPairCommFunction("com.odk.pairpongsender");
		commFun.setContext(getApplicationContext());
		if (commFun.isTablet()) {
            commFunRegistered = true;
            commFun.registerReceivers(this);
            initialize(new SceneMgr(new MainScene(commFun)));
		} else {
            commFunRegistered = false;
		    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    startActivity(intent);
		    initialize(new SceneMgr(null));
		}
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (commFunRegistered)
            commFun.unregisterReceivers(this);
    }
}
