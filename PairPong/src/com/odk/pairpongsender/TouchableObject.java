package com.odk.pairpongsender;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class TouchableObject {
	protected Rect position;
	protected Bitmap picture;
	TouchableObject(int x, int y ,int height, Bitmap p){
		picture=p;
		int width = height*p.getWidth()/p.getHeight();
		position= new Rect(x-width/2,y-height/2,x+width/2,y+height/2);
	    picture.setHasAlpha(true);
	}

	final void Draw(Canvas canvas, Paint paint){
	    predraw(canvas, paint);
		canvas.drawBitmap(picture, null, position, paint);
	    postdraw(canvas, paint);
	}

    final boolean isTouched(int x, int y){
        Boolean s = pretest();
        if (s != null) {
            return s;
        }
		if (x>position.left&&x<position.right&&y>position.top&&y<position.bottom) {
			return true;
		} else {
            return false;
		}
	}

	protected void postdraw(Canvas canvas, Paint paint) { }
    protected void predraw(Canvas canvas, Paint paint) { }
    protected Boolean pretest() { return null; }
}
