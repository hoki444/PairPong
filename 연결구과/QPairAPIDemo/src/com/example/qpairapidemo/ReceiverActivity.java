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
import java.util.ArrayList;
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
    	public boolean[] getboolarray(){
    		return ReceiverBroadcastReceiver.boolarrayvalue;
    	}
    	public boolean getbool(){
    		return ReceiverBroadcastReceiver.boolvalue;
    	}
    	public byte[] getbytearray(){
    		return ReceiverBroadcastReceiver.bytearrayvalue;
    	}
    	public byte getbyte(){
    		return ReceiverBroadcastReceiver.bytevalue;
    	}
    	public char[] getchararray(){
    		return ReceiverBroadcastReceiver.chararrayvalue;
    	}
    	public CharSequence getcharsequence(){
    		return ReceiverBroadcastReceiver.charsequencevalue;
    	}
    	public char getchar(){
    		return ReceiverBroadcastReceiver.charvalue;
    	}
    	public double[] getdoublearray(){
    		return ReceiverBroadcastReceiver.doublearrayvalue;
    	}
    	public double getdouble(){
    		return ReceiverBroadcastReceiver.doublevalue;
    	}
    	public float[] getfloatarray(){
    		return ReceiverBroadcastReceiver.floatarrayvalue;
    	}
    	public float getfloat(){
    		return ReceiverBroadcastReceiver.floatvalue;
    	}
    	public int[] getintarray(){
    		return ReceiverBroadcastReceiver.intarrayvalue;
    	}
    	public int getint(){
    		return ReceiverBroadcastReceiver.intvalue;
    	}
    	public long[] getlongarray(){
    		return ReceiverBroadcastReceiver.longarrayvalue;
    	}
    	public long getlong(){
    		return ReceiverBroadcastReceiver.longvalue;
    	}
    	public String[] getstringarray(){
    		return ReceiverBroadcastReceiver.stringarrayvalue;
    	}
    	public ArrayList<String> getstringarraylist(){
    		return ReceiverBroadcastReceiver.stringarraylistvalue;
    	}
    	public String getstring(){
    		return ReceiverBroadcastReceiver.stringvalue;
    	}
    }
}
