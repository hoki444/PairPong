package com.odk.pairpong.comm.game;

public class CommOption {
	public int racketSize;
	public int gameMode;
	public int soundMode;
	public int specialMode;
	public int[] highScores = new int[5];
	public CommOption () {
	}
	
	public void setFromODKStyleArray(int [] arr) {
	    racketSize = arr[0];
	    gameMode = arr[1];
	    soundMode = arr[2];
	    specialMode = arr[3];
	    for (int idx = 0; idx < 5; idx++) {
            highScores[idx] = arr[4 + idx];
	    }
	    
	}
	public int [] getODKStyleArray() {
	    int[] result = new int[9];
	    result[0] = racketSize;
	    result[1] = gameMode;
	    result[2] = soundMode;
	    result[3] = specialMode;
	    for (int idx = 0; idx < 5; idx++) {
	        result[idx + 4] = highScores[idx];
	    }
	    return result;
	}
}