package com.odk.pairpongsender;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.odk.pairpongsender.MainActivity.ModeType;

public class MainScreen {
	int textsize;

	TouchableObject[] tobject= new TouchableObject[5];
	MainScreen(TouchableObject title,TouchableObject start, TouchableObject option, TouchableObject highscore, TouchableObject exit){
		tobject[0]=title;
		tobject[1]=start;
		tobject[2]=option;
		tobject[3]=highscore;
		tobject[4]=exit;
	}
	public void Draw(Canvas canvas, Paint pnt, int x, int y,Resources res) {
		for(int i=0; i<5;i++){
			tobject[i].Draw(canvas, pnt);
		}
	}
	public ModeType TouchEvent(MotionEvent event) {
		if(tobject[0].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Main;
		if(tobject[1].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.DestinedToPlay;
		if(tobject[2].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Option;
		if(tobject[3].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Highscore;
		if(tobject[4].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Exit;
		return ModeType.Main;
	}
}
