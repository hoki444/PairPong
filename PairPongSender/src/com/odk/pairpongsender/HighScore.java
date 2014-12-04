package com.odk.pairpongsender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class HighScore {
	int textsize;
	ScoreList scorelist;
	public HighScore(ScoreList slist){
		scorelist = slist;
	}
	public void Draw(Canvas canvas, Paint pnt, int x, int y) {
		textsize=Math.min(x/8,y/6);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.BLACK);
		canvas.drawText("High Score", x/2-textsize*2.48f, y/10, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		canvas.drawText("Rank", x/12, y*3/12, pnt);
		canvas.drawText("Score", x*3/12, y*3/12, pnt);
		canvas.drawText("Option", x*5/12, y*3/12, pnt);
		canvas.drawText("Name", x*7/12, y*3/12, pnt);
		canvas.drawText("Date", x*9/12, y*3/12, pnt);
		for(int n=0;n<5;n++){
			canvas.drawText(String.valueOf(n), x/12, y*(4+n)/12, pnt);
			canvas.drawText(String.valueOf(scorelist.scores[n].score), x*3/12, y*(4+n)/12, pnt);
			canvas.drawText(scorelist.scores[n].name, x*7/12, y*(4+n)/12, pnt);
			canvas.drawText(String.valueOf(scorelist.scores[n].date), x*9/12, y*(4+n)/12, pnt);
		}
		canvas.drawRect(x/3, y*5/6, x*2/3, y*19/20, pnt);		
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("Exit", x/2-textsize*1f, y*109/120, pnt);
	}
	public int TouchEvent(MotionEvent event, int x, int y) {
		if(event.getX()>x/3&&event.getX()<x*2/3&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(event.getY()>y*5/6&&event.getY()<y*19/20){
				return 1;
			}
			return 4;
		}
		return 4;
	}

}
