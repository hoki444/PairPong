package com.odk.pairpongsender;

import com.odk.pairpongsender.MainActivity.ModeType;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class OptionScreen {
	int textsize;
	public void Draw(Canvas canvas, Paint pnt, int x, int y,Resources res) {
		Bitmap check = ((BitmapDrawable)res.getDrawable(R.drawable.check)).getBitmap();
		textsize=Math.min(x/8,y/6);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("Option", x/2-textsize*1.47f, y/6, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		canvas.drawText("Racket size", x/12, y/4, pnt);
		canvas.drawText("Large", x*2/20, y/3, pnt);
		canvas.drawText("Regular", x*6/20, y/3, pnt);
		canvas.drawText("Small", x*10/20, y/3, pnt);
		canvas.drawText("Bonus Score", x/12, y*5/12, pnt);
		canvas.drawText("Velocity", x*2/20, y/2, pnt);
		canvas.drawText("Accuracy", x*6/20, y/2, pnt);
		canvas.drawText("Both", x*10/20, y/2, pnt);
		canvas.drawText("Game mode", x/12, y*7/12, pnt);
		canvas.drawText("Classic", x*2/20, y*2/3, pnt);
		canvas.drawText("Servive", x*6/20, y*2/3, pnt);
		canvas.drawText("InfCombo", x*10/20, y*2/3, pnt);
		for(int n=0;n<3;n++){
			canvas.drawRect(x*9/40, y*(17+10*n)/60, x*11/40, y*(2+n)/6, pnt);
			canvas.drawRect(x*17/40, y*(17+10*n)/60, x*19/40, y*(2+n)/6, pnt);
			canvas.drawRect(x*25/40, y*(17+10*n)/60, x*27/40, y*(2+n)/6, pnt);
			canvas.drawBitmap(check, null, new Rect(x*(9+8*MainActivity.options[n])/40, y*(17+10*n)/60,
					x*(11+8*MainActivity.options[n])/40, y*(2+n)/6), pnt);
		}
		canvas.drawRect(x/3, y*5/6, x*2/3, y*19/20, pnt);
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public ModeType TouchEvent(MotionEvent event, int x, int y) {
		for(int l=0;l<3;l++){
			if(event.getY()>y*(16+10*l)/60&&event.getY()<y*(21+10*l)/60&&event.getAction()==MotionEvent.ACTION_DOWN){
				for(int n=0;n<3;n++){
					if(event.getX()>x*(8+8*n)/40&&event.getX()<x*(11+8*n)/40){
						MainActivity.options[l]=n;
					}
				}
			}
		}
		if(event.getX()>x/3&&event.getX()<x*2/3&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return ModeType.Main;
			}
			return ModeType.Option;
		}
		return ModeType.Option;
	}
}
