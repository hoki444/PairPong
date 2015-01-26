package com.odk.pairpong;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.lge.qpair.api.r2.QPairConstants;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpong.comm.backend.QPairCommFunction.DeviceType;
import com.odk.pairpong.comm.backend.QPairCommFunction.QPairState;
import com.odk.pairpong.game.MainScene;
import com.odk.pairpongsender.MainActivity;

public class PairPongBoardActivity extends AndroidApplication {
	private QPairCommFunction commFun;
	private boolean commFunRegistered;
	
	private boolean checkQPairPermission() {
        int res = checkCallingOrSelfPermission(QPairConstants.PERMISSION_USE_QPAIR_SERVICE);
        return PackageManager.PERMISSION_GRANTED == res;
	}
	
	private static int minMajorVersion = 4;
	private static int minMijorVersion = 0;
	private static int minMinorVersion = 26;
	private static String minVersionString = "4.0.26";
	

	private boolean confirmVersion(String version) {
	    String [] literals = version.split("\\.");
	    
	    if (literals.length != 3) {
	        System.out.println("Ill-formed version string: " + version);
	        return false;
	    }
	    
	    int major, mijor, minor;
	    try {
            major = Integer.parseInt(literals[0]);
            mijor = Integer.parseInt(literals[1]);
            minor = Integer.parseInt(literals[2]);
	    } catch (NumberFormatException e) {
	        System.out.println("one of string of version number is not a number: " + version);
	        return false;
	    }
	    return major > minMajorVersion || 
	           (major == minMajorVersion &&
	            (mijor > minMijorVersion ||
	             mijor == minMijorVersion && minor >= minMinorVersion));
	                    
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        

		commFun = new QPairCommFunction("com.odk.pairpong");// new QPairCommFunction("com.odk.pairpongsender");
		commFun.setContext(getApplicationContext());
		
		

		String qpairVersion = commFun.getQPairVersion();
		if (qpairVersion == null) {
		    Toast.makeText(this, "This app requires LG QPair to run. " +
                                 "Please install it first and then reinstall this",
                           Toast.LENGTH_LONG).show();
		    finish();
		    return;
		}

		if (!checkQPairPermission()) {
		    Toast.makeText(this, "Please confirm LG QPair is installed and reinstall this app", Toast.LENGTH_LONG).show();
		    finish();
		    return;
		}

		if (!confirmVersion(qpairVersion)) {
		    Toast.makeText(this, "This version of QPair is outdated. " +
                                 "The minimum required version is '" +
                                 minVersionString   + 
                                 "'. Please upgrade or reinstall it", 
                           Toast.LENGTH_LONG).show();
		    finish();
		    return;
		}
		String deviceModel = Build.MODEL;
		
		DeviceType deviceType = commFun.getDeviceType();
		if ("LG-V400".equals(deviceModel) || 
		    "LG-V700n".equals(deviceModel) || 
		    deviceType == DeviceType.Tablet) {
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            commFunRegistered = true;
            commFun.registerReceivers(this);
            initialize(new SceneMgr(new MainScene(commFun)));
		} else if (deviceType == DeviceType.Phone) {
            commFunRegistered = false;
		    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		    startActivity(intent);
		    finish();
		    initialize(new SceneMgr(null));
		} else {
		    Toast.makeText(this, "Please configure LG QPair to execute the app", Toast.LENGTH_LONG).show();
		    finish();
		}
    }
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	    super.onActivityResult(arg0, arg1, arg2);
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (commFunRegistered)
            commFun.unregisterReceivers(this);
    }
}
