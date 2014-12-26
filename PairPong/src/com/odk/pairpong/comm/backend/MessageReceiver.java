package com.odk.pairpong.comm.backend;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.badlogic.gdx.utils.Json;
import com.odk.pairpong.comm.general.MessageListener;


@SuppressWarnings("rawtypes")
public class MessageReceiver extends BroadcastReceiver {
    private HashMap<String, MessageListener> lisnrMap;
    private Json json = new Json();
    
    public MessageReceiver () {
    }
    public MessageReceiver (HashMap<String, MessageListener> lisnrMap) {
        this.lisnrMap = lisnrMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("message");
        String msgType = intent.getStringExtra("messageType");
        
        MessageListener lisnr;
        synchronized (lisnrMap) {
             lisnr = lisnrMap.get(msgType);
        }

        if (lisnr == null) {
            Log.w(this.getClass().getName(), "Received unknown type of message: " + msgType);
        } else {
            Object receivedObject = json.fromJson(lisnr.getTypeClass(), msg);
            lisnr.onReceive(receivedObject);
        }
    };
    
}