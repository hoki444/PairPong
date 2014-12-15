package com.odk.pairpongsender;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Option {
	int textsize;
	public void Draw(Canvas canvas, Paint pnt, int x, int y,Resources res) {
		Bitmap check = ((BitmapDrawable)res.getDrawable(R.drawable.check)).getBitmap();
		textsize=Math.min(x/8,y/6);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("Option", x/2-textsize*2.23f, y/5, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		canvas.drawText("Racket size", x/6, y/3, pnt);
		canvas.drawText("Large", x*2/20, y/2, pnt);
		canvas.drawText("regular", x*7/20, y/2, pnt);
		canvas.drawText("small", x*12/20, y/2, pnt);
		canvas.drawRect(x*5/20, y*9/20, x*6/20, y/2, pnt);
		canvas.drawRect(x*10/20, y*9/20, x*11/20, y/2, pnt);
		canvas.drawRect(x*15/20, y*9/20, x*16/20, y/2, pnt);
		canvas.drawBitmap(check, null, new Rect(x*(5+5*MainActivity.options[0])/20, y*9/20, x*(6+5*MainActivity.options[0])/20, y/2), pnt);
		canvas.drawRect(x/3, y*5/6, x*2/3, y*19/20, pnt);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public String TouchEvent(MotionEvent event, int x, int y) {
		if(event.getY()>y*9/20&&event.getY()<y/2&&event.getAction()==MotionEvent.ACTION_DOWN){
			for(int n=0;n<3;n++){
				if(event.getX()>x*(5+5*n)/20&&event.getY()<x*(6+5*n)/20){
					MainActivity.options[0]=n;
				}
			}
		}
		if(event.getX()>x/3&&event.getX()<x*2/3&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return "main";
			}
			return "option";
		}
		return "option";
	}
}
