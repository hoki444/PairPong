package com.odk.pairpongsender;

import com.odk.pairpong.R;
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
	TouchableObject texit;
	TouchableObject[] nselecteds = new TouchableObject[9];
	TouchableObject[] selecteds = new TouchableObject[9];
	boolean bgmon=true;
	boolean effecton=true;
	
	OptionScreen(TouchableObject exit,Resources res, int x, int y){
		for(int i=0; i<2; i++){
			for(int j=0; j<3; j++){
				nselecteds[3*i+j]= new TouchableObject(x*(12+10*j)/40, (int)(y*(19+10*i)/60), y*3/60, 
					((BitmapDrawable)res.getDrawable(R.drawable.nselected)).getBitmap());
				selecteds[3*i+j]= new TouchableObject(x*(12+10*j)/40, (int)(y*(19+10*i)/60), y*3/60, 
						((BitmapDrawable)res.getDrawable(R.drawable.selected)).getBitmap());
			}
		}
		nselecteds[6]= new TouchableObject(x*14/40, (int)(y*39/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.off)).getBitmap());
		selecteds[6]= new TouchableObject(x*14/40, (int)(y*39/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.on)).getBitmap());
		nselecteds[7]= new TouchableObject(x*30/40, (int)(y*39/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.off)).getBitmap());
		selecteds[7]= new TouchableObject(x*30/40, (int)(y*39/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.on)).getBitmap());
		nselecteds[8]= new TouchableObject(x*19/40, (int)(y*47/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.off)).getBitmap());
		selecteds[8]= new TouchableObject(x*19/40, (int)(y*47/60), y*3/60, 
				((BitmapDrawable)res.getDrawable(R.drawable.on)).getBitmap());
		texit=exit;
		if(MainActivity.options[2]%2==1)
			bgmon=false;
		if(MainActivity.options[2]>1)
			effecton=false;
	}
	public void Draw(Canvas canvas, Paint pnt, int x, int y,Resources res) {
		textsize=Math.min(x/8,y/6);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Option", x/2-textsize*1.47f, y/6, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		canvas.drawText("Racket size", x/12, y/4, pnt);
		canvas.drawText("Large", x*2/20, y/3, pnt);
		canvas.drawText("Regular", x*7/20, y/3, pnt);
		canvas.drawText("Small", x*12/20, y/3, pnt);
		canvas.drawText("Game mode", x/12, y*5/12, pnt);
		canvas.drawText("Classic", x*2/20, y/2, pnt);
		canvas.drawText("Servive", x*7/20, y/2, pnt);
		canvas.drawText("InfCombo", x*12/20, y/2, pnt);
		canvas.drawText("Sound mode", x/12, y*7/12, pnt);
		canvas.drawText("bgm", x*2/20, y*2/3, pnt);
		canvas.drawText("effect", x*10/20, y*2/3, pnt);
		if(MainActivity.options[3]!=0){
			canvas.drawText("Special mode", x/12, y*19/24, pnt);
			nselecteds[8].Draw(canvas, pnt);
		}
		for(int n=0;n<8;n++){
			nselecteds[n].Draw(canvas, pnt);
		}
		for(int n=0;n<2;n++){
			selecteds[3*n+MainActivity.options[n]].Draw(canvas, pnt);
		}
		if(bgmon)
			selecteds[6].Draw(canvas, pnt);
		if(effecton)
			selecteds[7].Draw(canvas, pnt);
		if(MainActivity.options[3]==2)
			selecteds[8].Draw(canvas, pnt);
		texit.Draw(canvas, pnt);
	}
	public ModeType TouchEvent(MotionEvent event, int x, int y) {
		for(int l=0;l<2;l++){
			for(int n=0;n<3;n++){
				if(selecteds[3*l+n].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
					MainActivity.options[l]=n;
			}
		}
		if(selecteds[6].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			bgmon=!bgmon;
		if(selecteds[7].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			effecton=!effecton;
		MainActivity.options[2]=0;
		if(!bgmon)
			MainActivity.options[2]++;
		if(!effecton)
			MainActivity.options[2]+=2;
		if(selecteds[8].isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN&&MainActivity.options[3]!=0){
				if(MainActivity.options[3]==1)
					MainActivity.options[3]=2;
				else
					MainActivity.options[3]=1;
		}
		if(texit.isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Main;
		return ModeType.Option;
	}
}
