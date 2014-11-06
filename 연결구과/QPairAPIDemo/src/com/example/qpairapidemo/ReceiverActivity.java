package com.example.qpairapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import com.lge.qpair.api.r1.QPairConstants;

public class ReceiverActivity extends Activity {
	receiverfunction rfunction= new receiverfunction();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver);
        testStart();
    }
    private TimerTask second;
    private TextView timer_text;
    private final Handler handler = new Handler();
    public void testStart() {
    		timer_text = (TextView)findViewById(R.id.timer);
    		second = new TimerTask() {

    			@Override
    			public void run() {
    				Update();
    			}
    		};
    		Timer timer = new Timer();
    		timer.schedule(second, 0, 100);
    }

    protected void Update() {
    		Runnable updater = new Runnable() {
    			public void run() {
    				timer_text.setText(String.valueOf(rfunction.getint()));
    			}
    		};
    		handler.post(updater);
    }
    private class receiverfunction{
    	public int getint(){
    		return ReceiverBroadcastReceiver.testint;
    	}
    }
}
