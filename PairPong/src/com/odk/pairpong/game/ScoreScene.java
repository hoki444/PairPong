package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.odk.pairpong.comm.game.CommConstants;
import com.odk.pairpong.comm.game.CommScore;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageCallback;

public class ScoreScene extends Scene {
    private CommFunction commFun;
    private int score;
	private BitmapFont bfont;
	private SpriteBatch batch;
	private String printingMsg = "Sending Score......";
	
	private MessageCallback callback = new MessageCallback() {
        @Override
        public void onSuccess() {
            SceneMgr.switchScene(new MainScene(commFun));
        }
        
        @Override
        public void onError(String reason) {
            synchronized (printingMsg) {
                printingMsg = "QPair Error: " + reason;
            }
        }
    };
    
    private void sendScore () {
        commFun.sendMessage(CommConstants.TYPE_SCORE, new CommScore(score), callback);
    }

	public ScoreScene (CommFunction commFun, int score) {
		super();
		this.commFun = commFun;
		this.score = score;
	}

	@Override
	public void prepare() {
    	bfont= new BitmapFont();
    	batch = new SpriteBatch();
        bfont.setColor(Color.WHITE); bfont.scale(3f);
        sendScore();
	}

	@Override
	public void postRender() {
		batch.begin();
    	bfont.setColor(Color.WHITE);
    	synchronized (printingMsg) {
            
            bfont.draw(batch, "Sending Score......", 400, 400);
        }
        batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void tearDown(){
    	batch.dispose();
        bfont.dispose();
	}


}
