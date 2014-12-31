package com.odk.pairpongsender;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.odk.pairpong.R;
import com.odk.pairpongsender.MainActivity.ModeType;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
public class Recoder {
	int textsize;
	int rank;
	String name;
	int myscore;
	char[] names = new char[8];
	int nowpointer=0;
	boolean showscore;
	ScoreList scorelist;
	TouchableObject texit;
	public Recoder(ScoreList slist, TouchableObject exit) {
		texit=exit;
		showscore=false;
		for(int n=0;n<8;n++)
			names[n]=' ';
		scorelist = slist;
		name="";
	}

	public void Draw(Canvas canvas, Paint pnt, int x, int y, int score, Resources res) {
		myscore=score;
		textsize=Math.min(x/8,y/6);
		textsize=textsize/2;
		pnt.setTextSize(textsize);
		pnt.setColor(Color.WHITE);
		canvas.drawText("High Score", x/2-textsize*2.48f, y/10, pnt);
        textsize=textsize/2;
        pnt.setTextSize(textsize);
        rank= scorelist.getRank(myscore);
        if (rank==6)
            showscore=true;
        if(showscore) {
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
                canvas.drawText(scorelist.scores[n].date, x*9/12, y*(4+n)/12, pnt);
            }
            canvas.drawText("Your Score : "+ String.valueOf(myscore), x/6, y*9/12, pnt);
            if(rank<6)
                canvas.drawText("You did rank "+ String.valueOf(rank)+"!", x/2, y*9/12, pnt);
    		texit.Draw(canvas, pnt);
        } else{
            canvas.drawText("You did rank "+ String.valueOf(rank)+"!", x/6, y/4, pnt);
            for(int n=0;n<8;n++){
                canvas.drawText(String.valueOf(names[n]), x*(12+n)/24, y/3, pnt);
                canvas.drawText("_", x*(12+n)/24, y/3+10, pnt);
            }
            for(int n=0;n<10;n++){
                canvas.drawText(String.valueOf(n), x*(1+n)/11, y/2, pnt);
                for(int m=0;m<3;m++){
                    if(10*m+n<26)
                        canvas.drawText(String.valueOf((char)(65+10*m+n)), x*(1+n)/11, y*(7+m)/12, pnt);
                }
            }
            canvas.drawText("<", x*7/11, y*9/12, pnt);
            canvas.drawText(">", x*8/11, y*9/12, pnt);
            canvas.drawText("del", x*9/11, y*9/12, pnt);
            canvas.drawText("_", x*(12+nowpointer)/24, y/3+5, pnt);
            canvas.drawText("Press Your Name :", x/6, y/3, pnt);
    		texit.Draw(canvas, pnt);
        } 
	}
	void pressData(int n,int m){
		if(m==-1)
			names[nowpointer]=(char)(48+n);
		if(10*m+n>=0 && 10*m+n<26)
			names[nowpointer]=(char)(65+10*m+n);
		if(10*m+n==28||10*m+n==29){
			names[nowpointer]=' ';
			if(nowpointer>0)
				nowpointer-=2;
		}
		if(10*m+n==26){
			if(nowpointer>0)
				nowpointer-=2;
		}
		if(nowpointer<7)
			nowpointer++;
	}
	public ModeType TouchEvent(MotionEvent event, int x, int y) {
		if(event.getX()>x/11&&event.getAction()==MotionEvent.ACTION_DOWN && !showscore){
			if(event.getY()>y*5/12&&event.getY()<y*9/12){
				pressData((int)(event.getX()*11/x)-1,(int)(event.getY()*12/y)-6);
			}
		}
		if(texit.isTouched((int)event.getX(), (int)event.getY())&&event.getAction()==MotionEvent.ACTION_DOWN){
			if(showscore){
				showscore=false;
				names = new char[8];
				for(int n=0;n<8;n++)
				names[n]=' ';
				nowpointer=0;
				return ModeType.Main;
			} else {
				name=String.valueOf(names);
				showscore=true;
				if(name=="MIKU    "||myscore==3939||(myscore>=39000&&myscore<40000))
					MainActivity.options[3]=1;
				scorelist.update(rank,myscore,name,
						new SimpleDateFormat("MM/dd HH:mm").format(new Date(System.currentTimeMillis())),
						String.valueOf(MainActivity.options[0])+String.valueOf(MainActivity.options[1])
								+String.valueOf(MainActivity.options[2]));
			}
		}
		return ModeType.Score;
	}
}
