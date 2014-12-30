package com.odk.pairpongsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.odk.pairpong.R;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpong.comm.game.CommConstants;
import com.odk.pairpong.comm.game.CommOption;
import com.odk.pairpong.comm.general.MessageCallback;

public class MainActivity extends Activity {
	public static int[] options = new int[9];
	private int[] scores = new int[5];
	private String[] names = new String[5];
	private String[] dates = new String[5];
	private String[] soptions = new String[5];
	private ScoreList slist;
	
	
    private MyView odkView;
    
	private QPairCommFunction commFun = new QPairCommFunction("com.odk.pairpong");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        commFun.registerReceivers(getApplicationContext());
        commFun.setContext(getApplicationContext());
        

		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
    	for(int n = 0; n < 5; n++) {
    		scores[n] = pref.getInt("KEY_SCORE"+String.valueOf(n), 5000-1000*n);
    		names[n] = pref.getString("KEY_NAME"+String.valueOf(n), "ODK");
    		soptions[n] = pref.getString("KEY_SOPTION"+String.valueOf(n), "100");
    		dates[n] = pref.getString("KEY_DATE"+String.valueOf(n), "12/04 15:51");
    		options[n+4] = scores[n];
    	}
		options[0] = pref.getInt("KEY_OPTION1", 0);
		options[1] = pref.getInt("KEY_OPTION2", 0);
		options[2] = pref.getInt("KEY_OPTION3", 0);
		options[3] = pref.getInt("KEY_OPTION4", 0);

    	slist = new ScoreList(scores, names, dates, soptions);
        odkView = new MyView(this);
        
        commFun.startActivity("com.odk.pairpong.PairPongBoardActivity", new MessageCallback() {
            @Override
            public void onSuccess() {
            }
            
            @Override
            public void onError(String reason) {
                System.out.println("PEER ACTIVITY NOT STARTED due to " + reason);
            }
        });
		setContentView(odkView);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        commFun.unregisterReceivers(getApplicationContext());
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK) {
	    	quitNow();
	    }
	    return true;
	}

    private void startGame() {
    	Intent intent = new Intent(this, ControllerActivity.class);
    	startActivityForResult(intent, 0);
    }

    public void quitNow() {
		finish();
	}

    public void savePref() {
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
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
    
    private int receivedScore = 0; // HACK

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            System.out.println("OK!! TO SCORE");
            receivedScore = data.getIntExtra("score", 0);
            odkView.mode = ModeType.Score;
        } else {
            System.out.println("BACK TO MAIN..");
            commFun.sendMessage(CommConstants.TYPE_CEASE_GAME, null, null);
            odkView.mode = ModeType.Main;
        } 
    }
    
    public static enum ModeType {
        Main, Option, Highscore, Score, DestinedToPlay, Play, Exit
    }
    class MyView extends View{
    	public ModeType mode;
    	Point dsize;
    	Display display;
    	Resources res = getResources();
    	Paint Pnt=new Paint();
    	MainScreen mscreen;
    	OptionScreen option;
    	Recoder recoder;
    	HighScore highscore;
		public MyView(Context context) {
			super(context);
			mode= ModeType.Main; 
			dsize = new Point(0,0);
			display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getSize(dsize);
			TouchableObject btntitle = new TouchableObject(dsize.x/2, (int)(dsize.y*4/20), dsize.y*5/20, 
					((BitmapDrawable)res.getDrawable(R.drawable.title)).getBitmap());
			TouchableObject btnexit = new TouchableObject(dsize.x/2, dsize.y*18/20, dsize.y*2/20, 
					((BitmapDrawable)res.getDrawable(R.drawable.btnexit)).getBitmap());
			TouchableObject btnhscore = new TouchableObject(dsize.x/2, dsize.y*15/20, dsize.y*2/20, 
					((BitmapDrawable)res.getDrawable(R.drawable.btnhighscore)).getBitmap());
			TouchableObject btnoption = new TouchableObject(dsize.x/2, dsize.y*12/20, dsize.y*2/20, 
					((BitmapDrawable)res.getDrawable(R.drawable.btnoption)).getBitmap());
			TouchableObject btnstart = new TouchableObject(dsize.x/2, dsize.y*9/20, dsize.y*2/20, 
					((BitmapDrawable)res.getDrawable(R.drawable.btngamestart)).getBitmap());
			mscreen = new MainScreen(btntitle,btnstart,btnoption,btnhscore,btnexit);
			recoder = new Recoder(slist,btnexit);
			option = new OptionScreen(btnexit);
			highscore = new HighScore(slist,btnexit);
		}
		public void onDraw(Canvas canvas) {
			Bitmap bground = ((BitmapDrawable)res.getDrawable(R.drawable.background)).getBitmap();
			canvas.drawBitmap(bground, null, new Rect(0, 0, dsize.x, dsize.y), Pnt);
			switch (mode) {
			case Main:
				mscreen.Draw(canvas, Pnt, dsize.x, dsize.y, res);
				break;
			case Option:
				option.Draw(canvas, Pnt, dsize.x, dsize.y, res);
			    break;
			case Highscore:
				highscore.Draw(canvas, Pnt, dsize.x, dsize.y, res);
			    break;
			case Score:
				recoder.Draw(canvas, Pnt, dsize.x, dsize.y, receivedScore, res);
			    break;
            default:
                break;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
            invalidate();
            if(event.getAction()==MotionEvent.ACTION_DOWN ||
               event.getAction()==MotionEvent.ACTION_MOVE) {
                final ModeType oldMode = mode;
                
                switch (mode) {
                case Main:
                    mode = mscreen.TouchEvent(event);
                    break;
                case Option:
                    mode = option.TouchEvent(event, dsize.x, dsize.y);
                    break;
                case Highscore:
                    mode = highscore.TouchEvent(event, dsize.x, dsize.y);
                    break;
                case Score:
                    mode = recoder.TouchEvent(event, dsize.x, dsize.y);
                    break;
                default:
                    break;
                }
                // check the changed value of "mode"
                if (mode != oldMode) {
                    switch (mode) {
                    case DestinedToPlay:
                        CommOption commOption = new CommOption();
                        commOption.setFromODKStyleArray(options);
                        System.out.println("TOUCHED Game start Button");
                        commFun.sendMessage(CommConstants.TYPE_START_GAME, commOption, 
                                new MessageCallback() {
                                    @Override
                                    public void onSuccess() {
                                        mode = ModeType.Play;
                                        startGame();
                                    }

                                    @Override
                                    public void onError(String reason) {
                                        Toast.makeText(getApplicationContext(), "Failed to start game: " + reason, Toast.LENGTH_LONG).show();
                                        mode = oldMode;
                                    }
                                });
                        break;
                    case Exit:
                        quitNow();
                        break;
                    case Main:
                        savePref();
                        System.out.println("TO MAIN");
                        break;
                    default:
                        break;
                    }
                }
                return true;
            }
            return false;
		}
    }
    
    
}