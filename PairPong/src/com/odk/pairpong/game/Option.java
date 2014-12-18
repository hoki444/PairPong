package com.odk.pairpong.game;

public class Option {
	int racketsize;
	int scoremode;
	int gamemode;
	int[] highscores;
	public Option(){
		highscores=new int[5];
	}
	public void setData(int[] data){
		if(data!=null && data.length>8){
			racketsize=data[0];
			scoremode=data[1];
			gamemode=data[2];
			for(int i=0;i<5;i++)
				highscores[i]=data[i+4];
		}
	}
}
