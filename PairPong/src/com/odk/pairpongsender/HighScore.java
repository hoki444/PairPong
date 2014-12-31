package com.odk.pairpongsender;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;

import com.odk.pairpongsender.MainActivity.ModeType;

public class HighScore {
	int textsize;
	ScoreList scorelist;
	TouchableObject texit;
	public HighScore(ScoreList slist, TouchableObject exit){
		scorelist = slist;
		texit = exit;
	}
	public void Draw(Canvas canvas, Paint pnt, int x, int y, Resources res) {
		textsize=Math.min(x/8,y/6);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("High Score", x/2-textsize*2.48f, y/10, pnt);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		canvas.drawText("Rank", x/12, y*3/12, pnt);
		canvas.drawText("Score", x*3/12, y*3/12, pnt);
		canvas.drawText("Option", x*5/12, y*3/12, pnt);
		canvas.drawText("Name", x*7/12, y*3/12, pnt);
		canvas.drawText("Date", x*9/12, y*3/12, pnt);
		for(int n=0;n<5;n++){
			canvas.drawText(String.valueOf(n+1), x/12, y*(4+n)/12, pnt);
			canvas.drawText(String.valueOf(scorelist.scores[n].score), x*3/12, y*(4+n)/12, pnt);
			canvas.drawText(scorelist.scores[n].option, x*5/12, y*(4+n)/12, pnt);
			canvas.drawText(scorelist.scores[n].name, x*7/12, y*(4+n)/12, pnt);
			canvas.drawText(String.valueOf(scorelist.scores[n].date), x*9/12, y*(4+n)/12, pnt);
		}
		texit.Draw(canvas, pnt);
	}
	public ModeType TouchEvent(MotionEvent event, int x, int y) {
		if(texit.isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN)
			return ModeType.Main;
		return ModeType.Highscore;
	}

}
