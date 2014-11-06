package com.example.qpairapidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBroadcastReceiver extends BroadcastReceiver {
	static int testint=0;
    public void onReceive(Context context, Intent intent) {
    	if(intent.getStringExtra("datakind").equals("int"))
    		testint = intent.getIntExtra("int", -1);
    }
}
