package com.odk.pairpongsender;

import android.os.Bundle;
import android.view.KeyEvent;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpongsender.game.MainGame;
import com.odk.pairpongsender.game.ReceiverFunction;
import com.odk.pairpongsender.game.SenderFunction;

public class ControllerActivity extends AndroidApplication {
	SenderFunction sfunction;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sfunction = new QPairSenderFunction(this);
		ReceiverFunction rfunction = new QPairReceiverFunction();

        sfunction.setpackage("com.odk.pairpong");
        initialize(new MainGame(sfunction, rfunction, this));
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK) {
	    	myDestroy();
	    }
	    return true;
	}
	public void myDestroy(){
		MainActivity.shutdown=true;
		super.onDestroy();
		System.exit(0);
	}
}
