package com.example.qpairapidemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReceiverService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startActivity(new Intent(this, ReceiverActivity.class)
                .putExtra("delivered_through", "service")
                .putExtra("original_intent", intent)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
        return START_NOT_STICKY;
    }
}
