package com.odk.pairpongsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.odk.pairpongsender.game.SenderFunction;

public class MainActivity extends Activity {
	SenderFunction sfunction= new QPairSenderFunction(this);
	MainActivity myactivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	myactivity=this;
        super.onCreate(savedInstanceState);
        View vw=new MyView(this);
		setContentView(vw);
    }
    public void Startgame() {
        // bind QPair Service
    	sfunction.setpackage("com.odk.pairpong");
    	sfunction.startreceiver("PairPongBoardActivity");
    	Intent intent = new Intent(this, ControllerActivity.class);
    	
    	startActivity(intent);
    }
    public void myDestroy(){
		super.onDestroy();
		System.exit(0);
	}
    
    class MyView extends View{
    	int mode;
    	Point dsize;
    	Display display;
    	Resources res = getResources();
    	Paint Pnt=new Paint();
    	MainScreen mscreen;
		public MyView(Context context) {
			super(context);
			mode=1;
			dsize = new Point(0,0);
			mscreen = new MainScreen();
			display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getSize(dsize);
			mHandler.sendEmptyMessage(0);
		}
		public void onDraw(Canvas canvas){
			
				canvas.drawColor(Color.LTGRAY);
				if(mode==1)
					mscreen.Draw(canvas, Pnt, dsize.x, dsize.y);
				if(mode==3)
					//option.Draw(canvas, Pnt, dsize.x, dsize.y);
				if(mode==4){}
					//highscore.Draw(canvas, Pnt, dsize.x, dsize.y, res);
				
		}
		public boolean onTouchEvent(MotionEvent event)
		{
				if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
					if(mode==1)
						mode=mscreen.TouchEvent(event, dsize.x, dsize.y);
					if(mode==3)
						//mode=option.TouchEvent(event, dsize.x, dsize.y);
					if(mode==4)
						//mode=highscore.TouchEvent(event, dsize.x, dsize.y);
					return true;
				}
			return false;
		}
		Handler mHandler = new Handler(){
			public void handleMessage(Message msg){
				invalidate();
				if(mode==2){
					mode=1;
					myactivity.Startgame();
				}
				if(mode==5)
					myactivity.myDestroy();
				mHandler.sendEmptyMessageDelayed(0, 33);
			}
		};
    }
}
