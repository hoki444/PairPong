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

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public class SenderActivity extends Activity implements View.OnClickListener {
	functioninfo finfo= new functioninfo();
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
                    if(finfo.whatsend=="boolarray")
                		i.putBooleanArrayExtra("boolarray", finfo.sendingboolarray);
                	else if(finfo.whatsend=="bool")
                		i.putBooleanExtra("bool", finfo.sendingbool);
                	else if(finfo.whatsend=="bytearray")
                		i.putByteArrayExtra("bytearray", finfo.sendingbytearray);
                	else if(finfo.whatsend=="byte")
                		i.putByteExtra("byte", finfo.sendingbyte);
                	else if(finfo.whatsend=="chararray")
                		i.putCharArrayExtra("chararray", finfo.sendingchararray);
                	else if(finfo.whatsend=="charsequence")
                		i.putCharSequenceExtra("charsequence", finfo.sendingcharsequence);
                	else if(finfo.whatsend=="char")
                		i.putCharExtra("char", finfo.sendingchar);
                	else if(finfo.whatsend=="doublearray")
                		i.putDoubleArrayExtra("doublearray", finfo.sendingdoublearray);
                	else if(finfo.whatsend=="double")
                		i.putDoubleExtra("double", finfo.sendingdouble);
                	else if(finfo.whatsend=="floatarray")
                		i.putFloatArrayExtra("floatarray", finfo.sendingfloatarray);
                	else if(finfo.whatsend=="float")
                		i.putFloatExtra("float", finfo.sendingfloat);
                	else if(finfo.whatsend=="intarray")
                		i.putIntArrayExtra("intarray", finfo.sendingintarray);
                	else if(finfo.whatsend=="int")
                		i.putIntExtra("int", finfo.sendingint);
                	else if(finfo.whatsend=="longarray")
                		i.putLongArrayExtra("longarray", finfo.sendinglongarray);
                	else if(finfo.whatsend=="long")
                		i.putLongExtra("long", finfo.sendinglong);
                	else if(finfo.whatsend=="stringarray")
                		i.putStringArrayExtra("stringarray", finfo.sendingstringarray);
                	else if(finfo.whatsend=="stringarraylist")
                		i.putStringArrayListExtra("stringarraylist", finfo.sendingstringarraylist);
                	else if(finfo.whatsend=="string")
                		i.putStringExtra("string", finfo.sendingstring);
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
    	public void sendboolarray(boolean[] boolarray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingboolarray=boolarray;
    		finfo.whatsend="boolarray";
    		callService();
    	}
    	public void sendbool(boolean sbool){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingbool=sbool;
    		finfo.whatsend="bool";
    		callService();
    	}
    	public void sendbytearray(byte[] sbytearray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingbytearray=sbytearray;
    		finfo.whatsend="bytearray";
    		callService();
    	}
    	public void sendbyte(byte sbyte){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingbyte=sbyte;
    		finfo.whatsend="byte";
    		callService();
    	}
    	public void sendchararray(char[] schararray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingchararray=schararray;
    		finfo.whatsend="chararray";
    		callService();
    	}
    	public void sendchar(char schar){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingchar=schar;
    		finfo.whatsend="char";
    		callService();
    	}
    	public void sendcharsequence(CharSequence sCharSequence){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingcharsequence=sCharSequence;
    		finfo.whatsend="charsequence";
    		callService();
    	}
    	public void senddoublearray(double[] sdoublearray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingdoublearray=sdoublearray;
    		finfo.whatsend="doublearray";
    		callService();
    	}
    	public void senddouble(double sdouble){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingdouble=sdouble;
    		finfo.whatsend="double";
    		callService();
    	}
    	public void sendfloatarray(float[] sfloatarray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingfloatarray=sfloatarray;
    		finfo.whatsend="floatarray";
    		callService();
    	}
    	public void sendfloat(float sfloat){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingfloat=sfloat;
    		finfo.whatsend="float";
    		callService();
    	}
    	public void sendintarray(int[] sintarray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingintarray=sintarray;
    		finfo.whatsend="intarray";
    		callService();
    	}
    	public void sendint(int sint){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingint=sint;
    		finfo.whatsend="int";
    		callService();
    	}
    	public void sendlongarray(long[] slongarray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendinglongarray=slongarray;
    		finfo.whatsend="longarray";
    		callService();
    	}
    	public void sendlong(long slong){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendinglong=slong;
    		finfo.whatsend="long";
    		callService();
    	}
    	public void sendstringarray(String[] sStringarray){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingstringarray=sStringarray;
    		finfo.whatsend="stringarray";
    		callService();
    	}
    	public void sendstringarraylist(ArrayList<String> sStringarraylist){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingstringarraylist=sStringarraylist;
    		finfo.whatsend="stringarraylist";
    		callService();
    	}
    	public void sendstring(String sString){
    		finfo.activityname="com.example.qpairapidemo/com.example.qpairapidemo.ReceiverBroadcastReceiver";
    		finfo.functionkind="senddata";
    		finfo.sendingstring=sString;
    		finfo.whatsend="string";
    		callService();
    	}
    }
    private class functioninfo{
    	String functionkind;
        boolean[] sendingboolarray;
        boolean sendingbool;
        byte[] sendingbytearray;
        byte sendingbyte;
        char[] sendingchararray;
        char sendingchar;
        CharSequence sendingcharsequence;
        double[] sendingdoublearray;
        double sendingdouble;
        float[] sendingfloatarray;
        float sendingfloat;
        int[] sendingintarray;
    	int sendingint;
    	long[] sendinglongarray;
    	long sendinglong;
    	String[] sendingstringarray;
    	ArrayList<String> sendingstringarraylist;
    	String sendingstring;
    	String activityname;
    	String whatsend;
    }
}
