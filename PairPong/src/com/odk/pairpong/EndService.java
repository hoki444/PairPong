package com.odk.pairpong;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EndService extends Service {
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PairPongBoardActivity.isstarting=false;
        return START_NOT_STICKY;
    }
}
