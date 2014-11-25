package com.odk.pairpong.game;

import java.util.ArrayList;

public interface SenderFunction {

	public abstract void setpackage(String p);

	public abstract void startreceiver(String activityname);

	public abstract void sendboolarray(boolean[] boolarray);

	public abstract void sendbool(boolean sbool);

	public abstract void sendbytearray(byte[] sbytearray);

	public abstract void sendbyte(byte sbyte);

	public abstract void sendchararray(char[] schararray);

	public abstract void sendchar(char schar);

	public abstract void sendcharsequence(CharSequence sCharSequence);

	public abstract void senddoublearray(double[] sdoublearray);

	public abstract void senddouble(double sdouble);

	public abstract void sendfloatarray(float[] sfloatarray);

	public abstract void sendfloat(float sfloat);

	public abstract void sendintarray(int[] sintarray);

	public abstract void sendint(int sint);

	public abstract void sendlongarray(long[] slongarray);

	public abstract void sendlong(long slong);

	public abstract void sendstringarray(String[] sStringarray);

	public abstract void sendstringarraylist(ArrayList<String> sStringarraylist);

	public abstract void sendstring(String sString);

}