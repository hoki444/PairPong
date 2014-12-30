package com.odk.pairpongsender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class TouchableObject {
	Rect position;
	Bitmap picture;
	TouchableObject(int x, int y ,int height, Bitmap p){
		picture=p;
		int width = height*p.getWidth()/p.getHeight();
		position= new Rect(x-width/2,y-height/2,x+width/2,y+height/2);
	}
	void Draw(Canvas canvas, Paint pnt){
		canvas.drawBitmap(picture, null , position, pnt);
	}
	boolean isTouched(int x, int y){
		if(x>position.left&&x<position.right&&y>position.top&&y<position.bottom)
			return true;
		return false;
	}
}
