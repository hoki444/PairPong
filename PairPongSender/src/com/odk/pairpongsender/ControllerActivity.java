package com.odk.pairpongsender;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ControllerActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		SenderFunction sfunction= new QPairSenderFunction(this);
        super.onCreate(savedInstanceState);
        sfunction.setpackage("com.odk.pairpong");
        MyView myview= new MyView(this,sfunction);
        setContentView(myview);
    }
	class MyView extends View{
		SenderFunction sfun;
		Paint Pnt=new Paint();
		float lastX;
		float lastY;
		double posX=0.45;
		double posY=0.45;
		Resources res = getResources();
		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		Point myPoint = new Point(0,0);
		MyView(Context context,SenderFunction sfunction){
			super(context);
			display.getSize(myPoint);
			mHandler.sendEmptyMessage(0);
			sfun=sfunction;
		}
		public void onDraw(Canvas canvas){
				Bitmap mainpicture = ((BitmapDrawable)res.getDrawable(R.drawable.board)).getBitmap();
				Bitmap point = ((BitmapDrawable)res.getDrawable(R.drawable.point)).getBitmap();
				canvas.drawBitmap(mainpicture, null, new Rect(0,0,myPoint.x,myPoint.y), Pnt);
				canvas.drawBitmap(point, null, new Rect((int)(posX*myPoint.x),(int)(posY*myPoint.y),
						(int)(posX*myPoint.x)+myPoint.x/10,(int)(posY*myPoint.y)+myPoint.y/6), Pnt);
		}
		public boolean onTouchEvent(MotionEvent event)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				lastX=event.getX();
				lastY=event.getY();
				return true;
			}
			if(event.getAction()==MotionEvent.ACTION_MOVE){
				posX=(float) (posX+(event.getX()-lastX)/myPoint.x);
				if(posX<0.055)
					posX=0.055;
				if(posX>0.85)
					posX=0.85;
				lastX=event.getX();
				posY=(float) (posY+(event.getY()-lastY)/myPoint.y);
				if(posY<0.095)
					posY=0.095;
				if(posY>0.738)
					posY=0.738;
				lastY=event.getY();
				return true;
			}
			return false;
		}
		Handler mHandler = new Handler(){
			public void handleMessage(Message msg){
				invalidate();
				double[] sdouble= new double[2];
				sdouble[0] = (posX-0.055)*4/0.795;
				sdouble[1] = (posY-0.095)*4/0.643;
				sfun.senddoublearray(sdouble);
				mHandler.sendEmptyMessageDelayed(0, 33);
			}
		};
	}
}
