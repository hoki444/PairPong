package com.odk.pairpongsender;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;

public class QPairSenderFunction implements SenderFunction {
	Activity myActivity;
	String packageName;
    private static final String CALLBACK_ACTION = "com.example.qpairapidemo.ACTION_CALLBACK";
	functioninfo finfo= new functioninfo();
	QPairSenderFunction(Activity activity){
		myActivity=activity;
	}
	void callService(){
		final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);

        // Bind to the QPair service
        boolean bindResult = myActivity.bindService(intent, new MyServiceConnection(), 0);

	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#setpackage(java.lang.String)
	 */
	@Override
	public void setpackage(String p){
		packageName=p;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#startreceiver(java.lang.String)
	 */
	@Override
	public void startreceiver(String activityname){
		finfo.activityname=packageName+"/"+packageName+"."+activityname;
		finfo.functionkind="startactivity";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendboolarray(boolean[])
	 */
	@Override
	public void sendboolarray(boolean[] boolarray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingboolarray=boolarray;
		finfo.whatsend="boolarray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendbool(boolean)
	 */
	@Override
	public void sendbool(boolean sbool){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingbool=sbool;
		finfo.whatsend="bool";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendbytearray(byte[])
	 */
	@Override
	public void sendbytearray(byte[] sbytearray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingbytearray=sbytearray;
		finfo.whatsend="bytearray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendbyte(byte)
	 */
	@Override
	public void sendbyte(byte sbyte){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingbyte=sbyte;
		finfo.whatsend="byte";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendchararray(char[])
	 */
	@Override
	public void sendchararray(char[] schararray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingchararray=schararray;
		finfo.whatsend="chararray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendchar(char)
	 */
	@Override
	public void sendchar(char schar){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingchar=schar;
		finfo.whatsend="char";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendcharsequence(java.lang.CharSequence)
	 */
	@Override
	public void sendcharsequence(CharSequence sCharSequence){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingcharsequence=sCharSequence;
		finfo.whatsend="charsequence";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#senddoublearray(double[])
	 */
	@Override
	public void senddoublearray(double[] sdoublearray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingdoublearray=sdoublearray;
		finfo.whatsend="doublearray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#senddouble(double)
	 */
	@Override
	public void senddouble(double sdouble){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingdouble=sdouble;
		finfo.whatsend="double";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendfloatarray(float[])
	 */
	@Override
	public void sendfloatarray(float[] sfloatarray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingfloatarray=sfloatarray;
		finfo.whatsend="floatarray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendfloat(float)
	 */
	@Override
	public void sendfloat(float sfloat){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingfloat=sfloat;
		finfo.whatsend="float";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendintarray(int[])
	 */
	@Override
	public void sendintarray(int[] sintarray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingintarray=sintarray;
		finfo.whatsend="intarray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendint(int)
	 */
	@Override
	public void sendint(int sint){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingint=sint;
		finfo.whatsend="int";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendlongarray(long[])
	 */
	@Override
	public void sendlongarray(long[] slongarray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendinglongarray=slongarray;
		finfo.whatsend="longarray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendlong(long)
	 */
	@Override
	public void sendlong(long slong){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendinglong=slong;
		finfo.whatsend="long";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendstringarray(java.lang.String[])
	 */
	@Override
	public void sendstringarray(String[] sStringarray){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingstringarray=sStringarray;
		finfo.whatsend="stringarray";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendstringarraylist(java.util.ArrayList)
	 */
	@Override
	public void sendstringarraylist(ArrayList<String> sStringarraylist){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingstringarraylist=sStringarraylist;
		finfo.whatsend="stringarraylist";
		callService();
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpongsender.SenderFunction#sendstring(java.lang.String)
	 */
	@Override
	public void sendstring(String sString){
		finfo.activityname=packageName+"/"+packageName+".ReceiveBroadcastReceiver";
		finfo.functionkind="senddata";
		finfo.sendingstring=sString;
		finfo.whatsend="string";
		callService();
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
            myActivity.unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
    BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String errorMessage = "error callback received due to "
                    + intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    };
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
