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
        View vw = new MyView(this);
		setContentView(vw);
    	sfunction.setpackage("com.odk.pairpong");
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK) {
	    	quitNow();
	    }
	    return true;
	}
    private void startGame() {
        // bind QPair Service
    	Intent intent = new Intent(this, ControllerActivity.class);
    	sfunction.startservice("StartService");
    	startActivity(intent);
    }
    public void quitNow() {
		super.onDestroy();
		finish();
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
    
    public static enum ModeType {
        Main, Option, Highscore, Score, DestinedToPlay, Play, Exit
    }
    class MyView extends View{
    	ModeType mode;
    	Point dsize;
    	Display display;
    	Resources res = getResources();
    	Paint Pnt=new Paint();
    	MainScreen mscreen;
    	OptionScreen option;
    	Recoder recoder;
    	HighScore highscore;
    	int score;
    	int tickAfterLoad;
		public MyView(Context context) {
			super(context);
			score=0;
			tickAfterLoad=0;
			mode= ModeType.Main; 
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
			switch (mode) {
			case Main:
				mscreen.Draw(canvas, Pnt, dsize.x, dsize.y);
				break;
			case Option:
				option.Draw(canvas, Pnt, dsize.x, dsize.y, res);
			    break;
			case Highscore:
				highscore.Draw(canvas, Pnt, dsize.x, dsize.y);
			    break;
			case Score:
				recoder.Draw(canvas, Pnt, dsize.x, dsize.y,score);
			    break;
			    
			}
		}

		public boolean onTouchEvent(MotionEvent event)
		{
			if(tickAfterLoad==35){
				if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
					ModeType oldMode = mode;
					if(mode == ModeType.Main)
						mode = mscreen.TouchEvent(event, dsize.x, dsize.y);
					if(mode == ModeType.Option) {
						mode=option.TouchEvent(event, dsize.x, dsize.y);
						if(mode == ModeType.Main) {
							savePref();
						}
					}
					if(mode == ModeType.Highscore)
						mode = highscore.TouchEvent(event, dsize.x, dsize.y);
					if(mode == ModeType.Score) {
						mode=recoder.TouchEvent(event, dsize.x, dsize.y);
						if(mode == ModeType.Main)
							savePref();
					}

					if(oldMode != mode)
						tickAfterLoad=15;
					return true;
				}
			}
			return false;
		}
		Handler mHandler = new Handler(){
			public void handleMessage(Message msg){
				invalidate();
				if (tickAfterLoad<35){
					tickAfterLoad++;
					if (rfunction.getint()==7){//���ھ�ȭ�� ��ȯ����
						mode = ModeType.Score;
					}
					if(tickAfterLoad == 10 && rfunction.getint() != 7)
				    	sfunction.startreceiver("PairPongBoardActivity");
					if(tickAfterLoad > 25)
						sfunction.sendintarray(options);
					else if (tickAfterLoad > 15) {
						sfunction.startservice("EndService");
					}
				} else {
					if(mode == ModeType.DestinedToPlay) {
						mode = ModeType.Play;
						startGame();
					}
					else if(mode == ModeType.Exit)
						myactivity.quitNow();
					else if (mode == ModeType.Play && shutdown){
						shutdown = false;
						mode = ModeType.Main;
						tickAfterLoad=15;
					}
					else if (mode == ModeType.Score) {
						if ((score=rfunction.getint())==7)//���ھ� �Է��� ������ �ʾҴ°�
					    	sfunction.startservice("EndService");//���ھ� �Է� ��û
						else
							sfunction.sendint(1);//���� ȭ������ ������
					}
				}
				this.sendEmptyMessageDelayed(0, 33);
			}
		};
    }
}
