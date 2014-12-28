package com.odk.pairpongsender;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpongsender.game.MainGame;

public class ControllerActivity extends AndroidApplication {
	private QPairCommFunction commFun;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		commFun = new QPairCommFunction("com.odk.pairpong");
		commFun.registerReceivers(this);
		commFun.setContext(getApplicationContext());

        initialize(new MainGame(commFun, this));
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK) {
	    	quit();
	    }
	    return true;
	}
	
	public void quit () {
	    setResult(RESULT_CANCELED);
	    finish();
	}

	public void quitWithScore(int score) {
	    Intent intent = new Intent();
	    intent.putExtra("score", score);
	    setResult(RESULT_OK, intent);
	    finish();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    commFun.unregisterReceivers(this);
	}
}
