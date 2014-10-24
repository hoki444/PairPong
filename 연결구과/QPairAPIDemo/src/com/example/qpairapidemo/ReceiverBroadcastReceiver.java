package com.example.qpairapidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
    	long pass = System.currentTimeMillis()-intent.getLongExtra("long", 1);
        context.startActivity(new Intent(context, ReceiverActivity.class)
        		.putExtra("passed_time", pass)
                .putExtra("delivered_through", "broadcast")
                .putExtra("original_intent", intent)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }
}
