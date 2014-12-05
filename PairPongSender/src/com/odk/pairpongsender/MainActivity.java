package com.odk.pairpongsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.odk.pairpongsender.game.ReceiverFunction;
import com.odk.pairpongsender.game.SenderFunction;

public class MainActivity extends Activity {
	SharedPreferences pref;
	int[] scores = new int[5];
	String[] names = new String[5];
	int[] dates = new int[5];
	ScoreList slist;
	SenderFunction sfunction= new QPairSenderFunction(this);
	ReceiverFunction rfunction= new QPairReceiverFunction();
	MainActivity myactivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		pref = getPreferences(Context.MODE_PRIVATE);
    	for(int n=0;n<5;n++){
    		scores[n]=pref.getInt("KEY_SCORE"+String.valueOf(n), 5000-1000*n);
    		names[n]=pref.getString("KEY_NAME"+String.valueOf(n), "ODK");
    		dates[n]=pref.getInt("KEY_DATE"+String.valueOf(n), 12041551);
    	}
    	slist = new ScoreList(scores,names,dates);
    	myactivity=this;
        super.onCreate(savedInstanceState);
        View vw=new MyView(this);
		setContentView(vw);
    	sfunction.setpackage("com.odk.pairpong");
    	sfunction.startreceiver("PairPongBoardActivity");
    }
    public void Startgame() {
        // bind QPair Service
    	Intent intent = new Intent(this, ControllerActivity.class);
    	sfunction.sendbool(true);
    	startActivity(intent);
    }
    public void myDestroy(){
		super.onDestroy();
		System.exit(0);
	}
    public void savePref(){
    	Editor editor = pref.edit();
    	for(int n=0;n<5;n++){
    		editor.remove("KEY_SCORE"+String.valueOf(n));
    		editor.putInt("KEY_SCORE"+String.valueOf(n), slist.scores[n].score);
    		editor.remove("KEY_NAME"+String.valueOf(n));
    		editor.putString("KEY_NAME"+String.valueOf(n), slist.scores[n].name);
    		editor.remove("KEY_DATE"+String.valueOf(n));
    		editor.putInt("KEY_DATE"+String.valueOf(n), slist.scores[n].date);
    	}
        editor.commit();
    }
    
    class MyView extends View{
    	int mode;
    	Point dsize;
    	Display display;
    	Resources res = getResources();
    	Paint Pnt=new Paint();
    	MainScreen mscreen;
    	Option option;
    	Recoder recoder;
    	HighScore highscore;
    	int score;
    	int loading;
		public MyView(Context context) {
			super(context);
			score=0;
			loading=0;
			mode=1;
			dsize = new Point(0,0);
			mscreen = new MainScreen();
			recoder = new Recoder(slist);
			option = new Option();
			highscore = new HighScore(slist);
			display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getSize(dsize);
			mHandler.sendEmptyMessage(0);
			
		}
		public void onDraw(Canvas canvas){
		
			canvas.drawColor(Color.LTGRAY);
			if(mode==1)
				mscreen.Draw(canvas, Pnt, dsize.x, dsize.y);
			if(mode==3)
				option.Draw(canvas, Pnt, dsize.x, dsize.y);
			if(mode==4)
				highscore.Draw(canvas, Pnt, dsize.x, dsize.y);
			if(mode==7)
				recoder.Draw(canvas, Pnt, dsize.x, dsize.y,score);
				
		}
		public boolean onTouchEvent(MotionEvent event)
		{
			if(loading==30){
				if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
					int pastmode=mode;
					if(mode==1)
						mode=mscreen.TouchEvent(event, dsize.x, dsize.y);
					if(mode==3)
						mode=option.TouchEvent(event, dsize.x, dsize.y);
					if(mode==4)
						mode=highscore.TouchEvent(event, dsize.x, dsize.y);
					if(mode==7){
						mode=recoder.TouchEvent(event, dsize.x, dsize.y);
						if(mode==1)
							savePref();
					}
					if(pastmode!=mode)
						loading=15;
					return true;
				}
			}
			return false;
		}
		Handler mHandler = new Handler(){
			public void handleMessage(Message msg){
				invalidate();
				if(loading<30){
					loading++;
					if(rfunction.getint()==7){
						mode=7;
						makename();
					}
				}
				else{
					if(mode==1){
					}
					else if(mode==2){
						mode=6;
						myactivity.Startgame();
					}
					else if(mode==5)
						myactivity.myDestroy();
					else if(mode==7){
						if((score=rfunction.getint())==7)
							sfunction.sendbool(false);
						else
							sfunction.sendint(1);
					}
				}
				mHandler.sendEmptyMessageDelayed(0, 33);
			}
		};
		void makename(){
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
		}
    }
}
