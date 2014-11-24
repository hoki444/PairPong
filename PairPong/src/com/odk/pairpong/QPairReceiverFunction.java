package com.odk.pairpong;

import java.util.ArrayList;

import com.odk.pairpong.ReceiveBroadcastReceiver;
import com.odk.pairpong.game.ReceiverFunction;

public class QPairReceiverFunction implements ReceiverFunction {
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getboolarray()
	 */
	@Override
	public boolean[] getboolarray(){
		return ReceiveBroadcastReceiver.boolarrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getbool()
	 */
	@Override
	public boolean getbool(){
		return ReceiveBroadcastReceiver.boolvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getbytearray()
	 */
	@Override
	public byte[] getbytearray(){
		return ReceiveBroadcastReceiver.bytearrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getbyte()
	 */
	@Override
	public byte getbyte(){
		return ReceiveBroadcastReceiver.bytevalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getchararray()
	 */
	@Override
	public char[] getchararray(){
		return ReceiveBroadcastReceiver.chararrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getcharsequence()
	 */
	@Override
	public CharSequence getcharsequence(){
		return ReceiveBroadcastReceiver.charsequencevalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getchar()
	 */
	@Override
	public char getchar(){
		return ReceiveBroadcastReceiver.charvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getdoublearray()
	 */
	@Override
	public double[] getdoublearray(){
		return ReceiveBroadcastReceiver.doublearrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getdouble()
	 */
	@Override
	public double getdouble(){
		return ReceiveBroadcastReceiver.doublevalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getfloatarray()
	 */
	@Override
	public float[] getfloatarray(){
		return ReceiveBroadcastReceiver.floatarrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getfloat()
	 */
	@Override
	public float getfloat(){
		return ReceiveBroadcastReceiver.floatvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getintarray()
	 */
	@Override
	public int[] getintarray(){
		return ReceiveBroadcastReceiver.intarrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getint()
	 */
	@Override
	public int getint(){
		return ReceiveBroadcastReceiver.intvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getlongarray()
	 */
	@Override
	public long[] getlongarray(){
		return ReceiveBroadcastReceiver.longarrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getlong()
	 */
	@Override
	public long getlong(){
		return ReceiveBroadcastReceiver.longvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getstringarray()
	 */
	@Override
	public String[] getstringarray(){
		return ReceiveBroadcastReceiver.stringarrayvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getstringarraylist()
	 */
	@Override
	public ArrayList<String> getstringarraylist(){
		return ReceiveBroadcastReceiver.stringarraylistvalue;
	}
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ReceiverFunction#getstring()
	 */
	@Override
	public String getstring(){
		return ReceiveBroadcastReceiver.stringvalue;
	}
}
