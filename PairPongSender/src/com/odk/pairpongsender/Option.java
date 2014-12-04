package com.odk.pairpongsender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Option {
	int textsize;
	public void Draw(Canvas canvas, Paint pnt, int x, int y) {
		textsize=Math.min(x/8,y/6);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("Pair Pong", x/2-textsize*2.23f, y/5, pnt);
		canvas.drawText("Comming Soon!", x/2-textsize*3.23f, y/2, pnt);
		canvas.drawRect(x/3, y*5/6, x*2/3, y*19/20, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public int TouchEvent(MotionEvent event, int x, int y) {
		if(event.getX()>x/3&&event.getX()<x*2/3&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return 1;
			}
			return 3;
		}
		return 3;
	}
}
