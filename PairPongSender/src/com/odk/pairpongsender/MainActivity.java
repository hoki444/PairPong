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
	static boolean shutdown=false;
	SharedPreferences pref;
	static int[] options = new int[1];
	int[] scores = new int[5];
	String[] names = new String[5];
	String[] dates = new String[5];
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
    		dates[n]=pref.getString("KEY_DATE"+String.valueOf(n), "12/04 15:51");
    	}
		options[0]=pref.getInt("KEY_OPTION1", 1);
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
    		editor.putString("KEY_DATE"+String.valueOf(n), slist.scores[n].date);
    	}
		editor.remove("KEY_OPTION1");
		editor.putInt("KEY_OPTION1", options[0]);
        editor.commit();
    }
    
    class MyView extends View{
    	String mode;
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
			mode="main";
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
			if(mode.equals("main"))
				mscreen.Draw(canvas, Pnt, dsize.x, dsize.y);
			if(mode.equals("option"))
				option.Draw(canvas, Pnt, dsize.x, dsize.y, res);
			if(mode.equals("highscore"))
				highscore.Draw(canvas, Pnt, dsize.x, dsize.y);
			if(mode.equals("score"))
				recoder.Draw(canvas, Pnt, dsize.x, dsize.y,score);
				
		}
		public boolean onTouchEvent(MotionEvent event)
		{
			if(loading==30){
				if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
					String pastmode=mode;
					if(mode.equals("main"))
						mode=mscreen.TouchEvent(event, dsize.x, dsize.y);
					if(mode.equals("option")){
						mode=option.TouchEvent(event, dsize.x, dsize.y);
						if(mode.equals("main")){
							savePref();
						}
					}
					if(mode.equals("highscore"))
						mode=highscore.TouchEvent(event, dsize.x, dsize.y);
					if(mode.equals("score")){
						mode=recoder.TouchEvent(event, dsize.x, dsize.y);
						if(mode.equals("main"))
							savePref();
					}
					if(!pastmode.equals(mode))
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
						mode="score";
						makename();
					}
					if(loading>15){
						sfunction.sendbool(false);
					}
				}
				else{
					if(mode.equals("main"))
						sfunction.sendintarray(options);
					if(mode.equals("play")){
						mode="playing";
						myactivity.Startgame();
					}
					else if(mode.equals("exit"))
						myactivity.myDestroy();
					else if(mode.equals("playing")&&shutdown){
						shutdown=false;
						mode="main";
						loading=15;
					}
					else if(mode.equals("score")){
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
