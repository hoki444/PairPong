package com.odk.pairpongsender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class MainScreen {
	int textsize;
	public void Draw(Canvas canvas, Paint pnt, int x, int y) {
		textsize=Math.min(x/8,y/6);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("Pair Pong", x/2-textsize*2.23f, y/5, pnt);
		canvas.drawRect(x/3, y/3, x*2/3, y*9/20, pnt);
		canvas.drawRect(x/3, y/2, x*2/3, y*37/60, pnt);
		canvas.drawRect(x/3, y*2/3, x*2/3, y*47/60, pnt);
		canvas.drawRect(x/3, y*5/6, x*2/3, y*19/20, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Game Start", x/2-textsize*2.27f, y*49/120, pnt);
		canvas.drawText("Option", x/2-textsize*1.5f, y*69/120, pnt);
		canvas.drawText("High Score", x/2-textsize*2.27f, y*89/120, pnt);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public int TouchEvent(MotionEvent event, int x, int y) {
		if(event.getX()>x/3&&event.getX()<x*2/3&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(event.getY()>y/3&&event.getY()<y*9/20){
				return 2;
			}
			if(event.getY()>y/2&&event.getY()<y*37/60){
				return 3;
			}
			if(event.getY()>y*2/3&&event.getY()<y*47/60){
				return 4;
			}
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return 5;
			}
			return 1;
		}
		return 1;
	}

}
