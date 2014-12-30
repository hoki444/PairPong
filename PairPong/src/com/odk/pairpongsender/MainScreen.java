package com.odk.pairpongsender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.odk.pairpongsender.MainActivity.ModeType;

public class MainScreen {
    MainActivity parent;
    public MainScreen (MainActivity parent) {
        this.parent = parent;
    }
	int textsize;
	public void Draw(Canvas canvas, Paint pnt, int x, int y) {
		textsize=Math.min(x/8,y/6);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("Pair Pong", x/2-textsize*2.23f, y/5, pnt);
		
		if (parent.isConnected) {
            pnt.setColor(Color.BLACK);
            canvas.drawRect(x/4, y/3, x*3/4, y*9/20, pnt);
		} else {
		    pnt.setColor(Color.GRAY);
            canvas.drawRect(x/4, y/3, x*3/4, y*9/20, pnt);
		}
        pnt.setColor(Color.BLACK);
		canvas.drawRect(x/4, y/2, x*3/4, y*37/60, pnt);
		canvas.drawRect(x/4, y*2/3, x*3/4, y*47/60, pnt);
		canvas.drawRect(x/4, y*5/6, x*3/4, y*19/20, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		
		if (parent.isConnected) {
            canvas.drawText("Game Start", x/2-textsize*2.27f, y*49/120, pnt);
		} else {
            canvas.drawText("Disonnected", x/2-textsize*2.77f, y*49/120, pnt);
		}
		canvas.drawText("Option", x/2-textsize*1.5f, y*69/120, pnt);
		canvas.drawText("High Score", x/2-textsize*2.27f, y*89/120, pnt);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public ModeType TouchEvent(MotionEvent event, int x, int y) {
		if(event.getX()>x/4&&event.getX()<x*3/4&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(parent.isConnected && event.getY()>y/3&&event.getY()<y*9/20){
				return ModeType.DestinedToPlay;
			}
			if(event.getY()>y/2&&event.getY()<y*37/60){
				return ModeType.Option;
			}
			if(event.getY()>y*2/3&&event.getY()<y*47/60){
				return ModeType.Play.Highscore;
			}
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return ModeType.Exit;
			}
			return ModeType.Main;
		}
		return ModeType.Main;
	}

}
