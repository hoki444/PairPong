package com.example.qpairapidemo;

import android.app.Activity;
import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;

import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public class SenderActivity extends Activity implements View.OnClickListener {
	functioninfo finfo= new functioninfo();
	int functionnum=0;
	senderfunction sfunction= new senderfunction();
    private static final String CALLBACK_ACTION = "com.example.qpairapidemo.ACTION_CALLBACK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        findViewById(R.id.sample_start_activity).setOnClickListener(this);
        findViewById(R.id.sample_send_broadcast).setOnClickListener(this);
        findViewById(R.id.sample_start_service).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
    }

    @Override
    public void onClick(View v) {
        // bind QPair Service
    	if(v==findViewById(R.id.sample_start_activity))
    		sfunction.startreceiver("com.example.qpairapidemo/com.example.qpairapidemo.ReceiverActivity");
    	else
    		sfunction.sendint(Integer.valueOf(((TextView)findViewById(R.id.sample_int_extra)).getText().toString()));
    }
    // ServiceConnection
    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            
			// get an IPeerContext
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent i = peerContext.newPeerIntent();

                i.setComponent(finfo.activityname);
                if(finfo.functionkind!="startactivity"){
                	i.putStringExtra("datakind", finfo.whatsend);
                	if(finfo.whatsend=="int")
                		i.putIntExtra("int", finfo.sendingint);
                }
                IPeerIntent callback = peerContext.newPeerIntent();
                
                // set callback action
                callback.setAction(CALLBACK_ACTION);
                if(finfo.functionkind=="startactivity")
                	peerContext.startActivityOnPeer(i, callback);
                else
                	peerContext.sendBroadcastOnPeer(i, callback);
            } catch (RemoteException e) {
            }

            // unbindService for each connection
            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

	// create a BroadcastReceiver for callback.
	// Application can get the callback for the messages sent to the peer, and
	// check whether the message sent to the peer succeed or not with the
	// callback messages.
    BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String errorMessage = "error callback received due to "
                    + intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    String[] strs(int id) {
        return str(id).split(",");
    }

    String str(int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    boolean has(int id) {
        return !"".equals(str(id).trim());
    }

    private class senderfunction{
    	void callService(){
    		final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);

            // Bind to the QPair service
            boolean bindResult = bindService(intent, new MyServiceConnection(), 0);

            if (!bindResult) {
                Toast.makeText(SenderActivity.this,
                        "Binding to QPair service have failed.",
                        Toast.LENGTH_SHORT).show();
            }

    	}
    	public void startreceiver(String activityname){
    		finfo.activityname=activityname;
    		finfo.functionkind="startactivity";
    		callService();
    	}
    	public void sendint(int sint){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingint=sint;
    		finfo.whatsend="int";
    		callService();
    	}
    }
    private class functioninfo{
    	String functionkind;
    	int sendingint;
    	String activityname;
    	String whatsend;
    }
}
