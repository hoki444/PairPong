package com.odk.pairpong;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpong.comm.backend.QPairCommFunction.DeviceType;
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
		
		DeviceType deviceType = commFun.getDeviceType();
		if (deviceType == DeviceType.Tablet) {
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            commFunRegistered = true;
            commFun.registerReceivers(this);
            initialize(new SceneMgr(new MainScene(commFun)));
		} else if (deviceType == DeviceType.Phone) {
            commFunRegistered = false;
		    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    startActivity(intent);
		    initialize(new SceneMgr(null));
		} else {
		    Toast.makeText(this, "Please configure LG QPair to execute the app", Toast.LENGTH_LONG).show();
		    finish();
		}
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (commFunRegistered)
            commFun.unregisterReceivers(this);
    }
}
