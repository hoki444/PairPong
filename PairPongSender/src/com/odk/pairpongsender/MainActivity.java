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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.odk.pairpongsender.game.ReceiverFunction;
import com.odk.pairpongsender.game.SenderFunction;

public class MainActivity extends Activity {
	static boolean shutdown=false;
	SharedPreferences pref;
	static int[] options = new int[9];
	int[] scores = new int[5];
	String[] names = new String[5];
	String[] dates = new String[5];
	String[] soptions = new String[5];
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
    		soptions[n]=pref.getString("KEY_SOPTION"+String.valueOf(n), "100");
    		dates[n]=pref.getString("KEY_DATE"+String.valueOf(n), "12/04 15:51");
    		options[n+4]=scores[n];
    	}
		options[0]=pref.getInt("KEY_OPTION1", 1);
		options[1]=pref.getInt("KEY_OPTION2", 0);
		options[2]=pref.getInt("KEY_OPTION3", 0);
		options[3]=pref.getInt("KEY_OPTION4", 0);
    	slist = new ScoreList(scores,names,dates,soptions);
    	myactivity=this;
        super.onCreate(savedInstanceState);
        View vw=new MyView(this);
		setContentView(vw);
    	sfunction.setpackage("com.odk.pairpong");
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK) {
	    	myDestroy();
	    }
	    return true;
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
    		editor.remove("KEY_SOPTION"+String.valueOf(n));
    		editor.putString("KEY_SOPTION"+String.valueOf(n), slist.scores[n].option);
    		editor.remove("KEY_DATE"+String.valueOf(n));
    		editor.putString("KEY_DATE"+String.valueOf(n), slist.scores[n].date);
    		options[n+4]=slist.scores[n].score;
    	}
		editor.remove("KEY_OPTION1");
		editor.putInt("KEY_OPTION1", options[0]);
		editor.remove("KEY_OPTION2");
		editor.putInt("KEY_OPTION2", options[1]);
		editor.remove("KEY_OPTION3");
		editor.putInt("KEY_OPTION3", options[2]);
		editor.remove("KEY_OPTION4");
		editor.putInt("KEY_OPTION4", options[3]);
        editor.commit();
    }
    
    class MyView extends View{
    	String mode;
    	Point dsize;
    	Display display;
    	Resources res = getResources();
    	Paint Pnt=new Paint();
    	MainScreen mscreen;
    	OptionScreen option;
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
			option = new OptionScreen();
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
			if(loading==35){
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
				if(loading<35){
					loading++;
					if(rfunction.getint()==7){//스코어화면 전환조건
						mode="score";
					}
					if(loading==10 && rfunction.getint()!=7)
				    	sfunction.startreceiver("PairPongBoardActivity");
					if(loading>25)
						sfunction.sendintarray(options);
					else if(loading>15)
						sfunction.sendbool(false);//태블릿에서 게임 실행중이면 종료
				}
				else{
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
						if((score=rfunction.getint())==7)//스코어 입력이 끝나지 않았는가
							sfunction.sendbool(false);//스코어 입력 요청
						else
							sfunction.sendint(1);//메인 화면으로 보내기
					}
				}
				mHandler.sendEmptyMessageDelayed(0, 33);
			}
		};
    }
}
