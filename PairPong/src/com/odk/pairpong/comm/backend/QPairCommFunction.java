package com.odk.pairpong.comm.backend;

import java.util.HashMap;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.badlogic.gdx.utils.Json;
import com.lge.qpair.api.r2.IPeerContext;
import com.lge.qpair.api.r2.IPeerIntent;
import com.lge.qpair.api.r2.QPairConstants;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageCallback;
import com.odk.pairpong.comm.general.MessageListener;

public class QPairCommFunction implements CommFunction, ContextSettable {
    private Json json = new Json();
	private Context context;
	private String peerPackage;
	
	public QPairCommFunction (String peerPackage) {
	    this.peerPackage = peerPackage;
	}
	
	
	
    private String getSuccessActionName () {
        return this.getClass().getPackage().getName() + ".ACTION_CALLBACK_SUCCESS";
    }

    private String getFailureActionName () {
        return this.getClass().getPackage().getName() + ".ACTION_CALLBACK_FAILURE";
    }
    
    private String getBroadcastActionName () { 
        return this.getClass().getPackage().getName() + ".ACTION_BC_MESSAGE";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
    
    public void registerReceivers(Context context) {
        context.registerReceiver(callbackReceiver, new IntentFilter(getFailureActionName()));
        context.registerReceiver(callbackReceiver, new IntentFilter(getSuccessActionName()));
        context.registerReceiver(messageReceiver, new IntentFilter(getBroadcastActionName()));
    }
    
    public void unregisterReceivers(Context context) {
        context.unregisterReceiver(callbackReceiver);
        context.unregisterReceiver(messageReceiver);
    }
    
	private boolean connect(ConnArg arg, MessageCallback callback) {
		final Intent intent = new Intent(QPairConstants.ACTION_SERVICE);
		
		if (callback != null) {
            synchronized (senderCallbackPendings) {
                senderCallbackPendings.put(arg.uuid, callback);
            } 
		}
        // Bind to the QPair service
        return context.bindService(intent.setPackage(QPairConstants.PACKAGE_NAME), new MyServiceConnection(arg), 0);
	}
	
	
    private static enum ConnType {
        Broadcast, Service, Activity
    }
	 // ServiceConnection
    private class MyServiceConnection implements ServiceConnection {
        private ConnArg connArg;
        public MyServiceConnection (ConnArg connArg) {
            this.connArg = connArg;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
			// get an IPeerContext
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                IPeerIntent callbackOnSuccess = peerContext.newPeerIntent();
                IPeerIntent callbackOnError = peerContext.newPeerIntent();
                
                callbackOnSuccess.setAction(getSuccessActionName());
                callbackOnSuccess.putStringExtra("uuid", connArg.uuid);
                callbackOnError.setAction(getFailureActionName());
                callbackOnError.putStringExtra("uuid", connArg.uuid);
                
                peerIntent.putLongExtra("timestamp", connArg.timestamp);

                String componentName;
                switch (connArg.type) {
                case Activity:
                    peerIntent.setPackage(connArg.peerPackage);
                    componentName = connArg.peerPackage + "/" + connArg.peerActivity;
                    peerIntent.setComponent(componentName);
                    peerContext.startActivityOnPeer(peerIntent, callbackOnSuccess, callbackOnError);
                    break;
                case Broadcast:
                    if (connArg.msg != null) {
                        peerIntent.putStringExtra("message", connArg.msg);
                        peerIntent.putStringExtra("messageType", connArg.msgType);
                    }
                    peerIntent.setPackage(connArg.peerPackage);
                    peerIntent.setAction(getBroadcastActionName());
                    peerContext.sendBroadcastOnPeer(peerIntent, callbackOnSuccess, callbackOnError);
                    break;
                case Service:
                    componentName = connArg.peerPackage + "/" + connArg.peerService;
                    peerIntent.setComponent(componentName);
                    peerContext.startServiceOnPeer(peerIntent, callbackOnSuccess, callbackOnError);
                    break;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // unbindService for each connection
            context.unbindService(this);
            
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @SuppressWarnings("rawtypes")
    private HashMap<String, MessageListener> lisnrMap = new HashMap<String, MessageListener>();
    private MessageReceiver messageReceiver = new MessageReceiver(lisnrMap);
    private HashMap<String, MessageCallback> senderCallbackPendings = new HashMap<String, MessageCallback>();
    private BroadcastReceiver callbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageCallback callback;
            String uuid = intent.getStringExtra("uuid");
            synchronized (senderCallbackPendings) {
                callback = senderCallbackPendings.get(uuid);
            }
            if (callback == null) {
                // pass
            } else if (getSuccessActionName().equals(intent.getAction())) {
                callback.onSuccess();
            } else if (getFailureActionName().equals(intent.getAction())) {
                String reason = intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
                callback.onError(reason);
            }
        }
    };

    private static class ConnArg {
        public ConnType type;
        public long timestamp;
        public String uuid;
        public String peerPackage;

        // fields for broadcasting
        public String msg;
        public String msgType;
        
        // fields for starting service on peer
        public String peerService;

        // fields for starting activity on peer
        public String peerActivity; 
        
        public ConnArg (ConnType type)  {
            this.type = type;
            this.uuid = UUID.randomUUID().toString();
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    @Override
    public void sendMessage(String msgType, Object obj, MessageCallback callback) {
        String msg = json.toJson(obj);
        ConnArg arg = new ConnArg(ConnType.Broadcast);
        arg.msg = msg;
        arg.peerPackage = peerPackage;
        arg.msgType = msgType;
        connect(arg, callback);
    }

    @Override
    public void startService(String serviceName, MessageCallback callback) {
        ConnArg arg = new ConnArg(ConnType.Service);
        arg.peerService = serviceName;
        arg.peerPackage = peerPackage;
        connect(arg, callback);
    }

    @Override
    public void startActivity(String activityName, MessageCallback callback) {
        ConnArg arg = new ConnArg(ConnType.Activity);
        arg.peerActivity = activityName;
        arg.peerPackage = peerPackage;
        connect(arg, callback);
    }

    @Override
    public <T> void registerListener(MessageListener<T> msgLisnr) {
        String typeName = msgLisnr.getTypeName();
        synchronized (lisnrMap) {
            lisnrMap.put(typeName, msgLisnr);
        }
    }

    @Override
    public <T> void unregisterListener(MessageListener<T> msgLisnr) {
        String typeName = msgLisnr.getTypeName();
        synchronized(lisnrMap) {
            lisnrMap.remove(typeName);
        }
    }
    
    private static String peelPackageName (String className) {
        int idx = className.lastIndexOf(".");
        if (idx > 0) 
            return className.substring(0, idx);
        else
            return "";
    }
}