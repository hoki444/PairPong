package com.odk.pairpongsender;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiveBroadcastReceiver extends BroadcastReceiver {
	static boolean[] boolarrayvalue;
	static boolean boolvalue;
	static byte[] bytearrayvalue;
	static byte bytevalue;
	static char[] chararrayvalue;
	static char charvalue;
	static CharSequence charsequencevalue;
	static double[] doublearrayvalue;
	static double doublevalue;
	static float[] floatarrayvalue;
	static float floatvalue;
	static int[] intarrayvalue;
	static int intvalue;
	static long[] longarrayvalue;
	static long longvalue;
	static String[] stringarrayvalue;
	static ArrayList<String> stringarraylistvalue;
	static String stringvalue;
    public void onReceive(Context context, Intent intent) {
    	if(intent.getStringExtra("datakind").equals("boolarray"))
    		boolarrayvalue = intent.getBooleanArrayExtra("boolarray");
    	else if(intent.getStringExtra("datakind").equals("bool"))
    		boolvalue = intent.getBooleanExtra("bool", true);
    	else if(intent.getStringExtra("datakind").equals("bytearray"))
    		bytearrayvalue = intent.getByteArrayExtra("bytearray");
    	else if(intent.getStringExtra("datakind").equals("byte"))
    		bytevalue = intent.getByteExtra("byte", (byte)1);
    	else if(intent.getStringExtra("datakind").equals("chararray"))
    		chararrayvalue = intent.getCharArrayExtra("chararray");
    	else if(intent.getStringExtra("datakind").equals("charsequence"))
    		charsequencevalue = intent.getCharSequenceExtra("charsequence");
    	else if(intent.getStringExtra("datakind").equals("char"))
    		charvalue = intent.getCharExtra("char", 'a');
    	else if(intent.getStringExtra("datakind").equals("doublearray"))
    		doublearrayvalue = intent.getDoubleArrayExtra("doublearray");
    	else if(intent.getStringExtra("datakind").equals("double"))
    		doublevalue = intent.getDoubleExtra("double", -1);
    	else if(intent.getStringExtra("datakind").equals("floatarray"))
    		floatarrayvalue = intent.getFloatArrayExtra("floatarray");
    	else if(intent.getStringExtra("datakind").equals("float"))
    		floatvalue = intent.getFloatExtra("float", -1);
    	else if(intent.getStringExtra("datakind").equals("intarray"))
    		intarrayvalue = intent.getIntArrayExtra("intarray");
    	else if(intent.getStringExtra("datakind").equals("int"))
    		intvalue = intent.getIntExtra("int", -1);
    	else if(intent.getStringExtra("datakind").equals("longarray"))
    		longarrayvalue = intent.getLongArrayExtra("longarray");
    	else if(intent.getStringExtra("datakind").equals("long"))
    		longvalue = intent.getLongExtra("long", -1);
    	else if(intent.getStringExtra("datakind").equals("stringarray"))
    		stringarrayvalue = intent.getStringArrayExtra("stringarray");
    	else if(intent.getStringExtra("datakind").equals("stringarraylist"))
    		stringarraylistvalue = intent.getStringArrayListExtra("stringarraylist");
    	else if(intent.getStringExtra("datakind").equals("string"))
    		stringvalue = intent.getStringExtra("string");
    }
}