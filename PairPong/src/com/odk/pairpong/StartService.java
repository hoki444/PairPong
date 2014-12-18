package com.odk.pairpong;

import com.odk.pairpong.PairPongBoardActivity;
import com.odk.pairpong.game.GameScene;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StartService extends Service {
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PairPongBoardActivity.isstarting=true;
        return START_NOT_STICKY;
    }
}
