package com.odk.pairpong.comm;

public class CommOption {
	public int racketSize;
	public int scoreMode;
	public int gameMode;
	public int[] highScores = new int[5];
	public CommOption () {
	}
	
	public void setFromODKStyleArray(int [] arr) {
	    racketSize = arr[0];
	    scoreMode = arr[1];
	    gameMode = arr[2];
	    for (int idx = 0; idx < 5; idx++) {
            highScores[idx] = arr[4 + idx];
	    }
	    
	}
	public int [] getODKStyleArray() {
	    int[] result = new int[9];
	    result[0] = racketSize;
	    result[1] = scoreMode;
	    result[2] = gameMode;
	    for (int idx = 0; idx < 5; idx++) {
	        result[idx + 4] = highScores[idx];
	    }
	    return result;
	}
}