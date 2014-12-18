package com.odk.pairpong;

import com.odk.pairpong.game.ServiceFunction;

public class PhoneServiceFunction implements ServiceFunction {
	/* (non-Javadoc)
	 * @see com.odk.pairpong.ServiceFunction#isstartstate()
	 */
	@Override
	public boolean isstartstate(){
		return PairPongBoardActivity.isstarting;
	}
}
