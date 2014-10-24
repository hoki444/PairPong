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
    private static final String T = SenderActivity.class.getSimpleName();
    private static final String CALLBACK_ACTION = "com.example.qpairapidemo.ACTION_CALLBACK";
    private String lastRequestUriPath;
    private ContentObserver contentObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        lastRequestUriPath = "/local/" + getPackageName() + "/last_request";

        findViewById(R.id.sample_start_activity).setOnClickListener(this);
        findViewById(R.id.sample_send_broadcast).setOnClickListener(this);
        findViewById(R.id.sample_start_service).setOnClickListener(this);

        // delete 'last_request' property by calling deleteQPairProperty()
        int numDeleted = deleteQPairProperty(QPairConstants.PROPERTY_SCHEME_AUTHORITY + lastRequestUriPath);
        ((TextView)findViewById(R.id.lastRequest)).setText(
                "return from delete(last_request): " + numDeleted);

        //For observing QPair Property
        contentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.parse(QPairConstants.PROPERTY_SCHEME_AUTHORITY + lastRequestUriPath),
                false,
                contentObserver);

		// get 'device name' property by calling getQPairProperty()
        ((TextView)findViewById(R.id.propertyValue)).setText(getQPairProperty(QPairConstants.PROPERTY_SCHEME_AUTHORITY + "/local/qpair/device_name"));

		// register callback receiver for callback intent
        registerReceiver(callbackReceiver, new IntentFilter(CALLBACK_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.propertyValue)).setText(
                "peer device name = " +
                getQPairProperty(QPairConstants.PROPERTY_SCHEME_AUTHORITY + "/peer/qpair/device_name"));
    }

    // get QPair Property
    private CharSequence getQPairProperty(String uriString) {
        Uri uri = Uri.parse(uriString);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);          
                }
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    // update QPair Property
    private int updateQPairProperty(String uriString, String value) {
        Uri uri = Uri.parse(uriString);
        ContentValues cv = new ContentValues();
        cv.put("", value);
        return getContentResolver().update(uri, cv, null, null);
    }

    //delete QPair property
    private int deleteQPairProperty(String uriString) {
        Uri uri = Uri.parse(uriString);
        return getContentResolver().delete(uri, null, null);
    }

    @Override
    protected void onDestroy() {
        //unregister callbackReceiver
        unregisterReceiver(callbackReceiver);
        //unregister ContentObserver
        getContentResolver().unregisterContentObserver(contentObserver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // bind QPair Service
        boolean r = bindService(new Intent(QPairConstants.ACTION_QPAIR_SERVICE), new MyServiceConnection(v.getId()), 0);
        if (!r) {
            Toast.makeText(this, "bindService() returned false.", Toast.LENGTH_SHORT).show();
        }
    }

    // ServiceConnection
    private class MyServiceConnection implements ServiceConnection {
        private final int componentTypeId;

        private MyServiceConnection(int componentTypeId) {
            this.componentTypeId = componentTypeId;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            
			// get an IPeerContext
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent i = peerContext.newPeerIntent();

                //make IPeerIntent and set data
                if (has(R.id.sample_action)) {
                    i.setAction(str(R.id.sample_action));
                }
                if (!"".equals(((Spinner)findViewById(R.id.sample_category)).getSelectedItem()
                        .toString())) {
                    i.addCategory(((Spinner)findViewById(R.id.sample_category))
                            .getSelectedItem().toString());
                }
                if (has(R.id.sample_data) && has(R.id.sample_type)) {
                    i.setDataAndType(str(R.id.sample_data), str(R.id.sample_type));
                } else if (has(R.id.sample_data)) {
                    i.setData(str(R.id.sample_data));
                } else if (has(R.id.sample_type)) {
                    i.setType(str(R.id.sample_type));
                }
                if (!"".equals(((Spinner)findViewById(R.id.sample_component)).getSelectedItem()
                        .toString())) {
                    i.setComponent(((Spinner)findViewById(R.id.sample_component))
                            .getSelectedItem().toString());
                }
                if (has(R.id.sample_packageName)) {
                    i.setPackage(str(R.id.sample_packageName));
                }

                if (has(R.id.sample_boolean_array_extra)) {
                    i.putBooleanArrayExtra("booleanArray",
                            Boolean_arrayOf(R.id.sample_boolean_array_extra));
                }
                if (has(R.id.sample_boolean_extra)) {
                    i.putBooleanExtra("boolean",
                            Boolean.valueOf(str(R.id.sample_boolean_extra)));
                }
                if (has(R.id.sample_byte_array_extra)) {
                    i.putByteArrayExtra("byteArray", Byte_arrayOf(R.id.sample_byte_array_extra));
                }
                if (has(R.id.sample_byte_extra)) {
                    i.putByteExtra("byte", Byte.valueOf(str(R.id.sample_byte_extra)));
                }
                if (has(R.id.sample_char_array_extra)) {
                    i.putCharArrayExtra("charArray", Char_arrayOf(R.id.sample_char_array_extra));
                }
                if (has(R.id.sample_char_extra)) {
                    i.putCharExtra("char", str(R.id.sample_char_extra).charAt(0));
                }
                if (has(R.id.sample_char_sequence_extra)) {
                    i.putCharSequenceExtra("charSequence", str(R.id.sample_char_sequence_extra));
                }
                if (has(R.id.sample_double_array_extra)) {
                    i.putDoubleArrayExtra("doubleArray",
                            Double_arrayOf(R.id.sample_double_array_extra));
                }
                if (has(R.id.sample_double_extra)) {
                    i.putDoubleExtra("double", Double.valueOf(str(R.id.sample_double_extra)));
                }
                if (has(R.id.sample_float_array_extra)) {
                    i.putFloatArrayExtra("floatArray",
                            Float_arrayOf(R.id.sample_float_array_extra));
                }
                if (has(R.id.sample_float_extra)) {
                    i.putFloatExtra("float", Float.valueOf(str(R.id.sample_float_extra)));
                }
                if (has(R.id.sample_int_array_extra)) {
                    i.putIntArrayExtra("intArray", Integer_arrayOf(R.id.sample_int_array_extra));
                }
                if (has(R.id.sample_int_extra)) {
                    i.putIntExtra("int", Integer.valueOf(str(R.id.sample_int_extra)));
                }
                if (has(R.id.sample_long_array_extra)) {
                    i.putLongArrayExtra("longArray", Long_arrayOf(R.id.sample_long_array_extra));
                }
                if (has(R.id.sample_long_extra)) {
                    i.putLongExtra("long", System.currentTimeMillis());
                }
                if (has(R.id.sample_string_array_extra)) {
                    i.putStringArrayExtra("stringArray", strs(R.id.sample_string_array_extra));
                }
                if (has(R.id.sample_string_array_list_extra)) {
                    i.putStringArrayListExtra("stringArrayList",
                            Arrays.asList(strs(R.id.sample_string_array_list_extra)));
                }
                if (has(R.id.sample_string_extra)) {
                    i.putStringExtra("string", str(R.id.sample_string_extra));
                }

                // create an IPeerIntent for callback intent
                IPeerIntent callback = peerContext.newPeerIntent();
                // set callback action
                callback.setAction(CALLBACK_ACTION);
                Log.w(T, "getting parameter that has not been set: " + i.getDataString());
                if (R.id.sample_start_activity == componentTypeId) {
                    Log.i(T, "start_activity: " + i.toString());
                    // for start_activity
                    peerContext.startActivityOnPeer(i, callback);
                } else if (R.id.sample_send_broadcast == componentTypeId) {
                    Log.i(T, "send_broadcast: " + i.toString());
                    // for send_broadcast
                    peerContext.sendBroadcastOnPeer(i, callback);
                } else if (R.id.sample_start_service == componentTypeId) {
                    Log.i(T, "start_service: " + i.toString());
                    // for start_service
                    peerContext.startServiceOnPeer(i, callback);
                }
                // update 'last_reqeust' property
                updateQPairProperty(QPairConstants.PROPERTY_SCHEME_AUTHORITY + lastRequestUriPath,
                        i.toStringInDetail());
            } catch (RemoteException e) {
                Log.e(T, e.toString());
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
            Log.i(T, errorMessage);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    private boolean[] Boolean_arrayOf(int id) {
        String[] ss = strs(id);
        boolean[] bb = new boolean[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Boolean.valueOf(ss[i]);
        }
        return bb;
    }

    private byte[] Byte_arrayOf(int id) {
        String[] ss = strs(id);
        byte[] bb = new byte[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Byte.valueOf(ss[i]);
        }
        return bb;
    }

    private char[] Char_arrayOf(int id) {
        String[] ss = strs(id);
        char[] bb = new char[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = ss[i].charAt(0);
        }
        return bb;
    }

    private double[] Double_arrayOf(int id) {
        String[] ss = strs(id);
        double[] bb = new double[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Double.valueOf(ss[i]);
        }
        return bb;
    }

    private float[] Float_arrayOf(int id) {
        String[] ss = strs(id);
        float[] bb = new float[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Float.valueOf(ss[i]);
        }
        return bb;
    }

    private int[] Integer_arrayOf(int id) {
        String[] ss = strs(id);
        int[] bb = new int[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Integer.valueOf(ss[i]);
        }
        return bb;
    }

    private long[] Long_arrayOf(int id) {
        String[] ss = strs(id);
        long[] bb = new long[ss.length];
        for (int i = 0; i < bb.length; ++i) {
            bb[i] = Long.valueOf(ss[i]);
        }
        return bb;
    }

    String[] strs(int id) {
        return str(id).split(",");
    }

    String str(int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    boolean has(int id) {
        return !"".equals(str(id).trim());
    }

	// declare a ContentObserver for observing QPair properties
    private class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            ((TextView) findViewById(R.id.lastRequest)).setText(
                    "last request = " + getQPairProperty(
                            QPairConstants.PROPERTY_SCHEME_AUTHORITY + lastRequestUriPath));
        }
    }
}
