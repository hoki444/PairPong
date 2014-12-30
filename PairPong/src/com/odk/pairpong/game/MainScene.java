package com.odk.pairpong.game;

import com.algy.schedcore.SchedTask;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.frontend.Scene;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.odk.pairpong.comm.game.PingListener;
import com.odk.pairpong.comm.general.CommFunction;

public class MainScene extends Scene {
	private BitmapFont bfont;
	private SpriteBatch batch;
    private CommFunction commFun;
    private boolean isConnected;
    
    private SchedTask statePoller = new SchedTask() {
        @Override
        public void onScheduled(SchedTime time) {
            isConnected = commFun.isConnected();
        }
        
        @Override
        public void endSchedule() {
        }
        
        @Override
        public void beginSchedule() {
        }
    };
    
    private GameStartListener lsnr;
	public MainScene(CommFunction commFun) {
		super();
	    this.commFun = commFun;
	    this.lsnr = new GameStartListener(commFun);
	    commFun.registerListener(new PingListener(commFun));
	}

	@Override
	public void prepare() {
    	bfont = new BitmapFont(Gdx.files.internal("yuppy_tc_45.fnt"));
    	batch = new SpriteBatch();
    	
    	commFun.registerListener(lsnr);
        schedule(0, 300, statePoller);
	}
	
	@Override
	public void postRender() {
		batch.begin();
        bfont.setColor(Color.WHITE); 
        if (isConnected) {
            bfont.draw(batch, "Start Game through the Smartphone App", 100, 400);
        } else {
            bfont.draw(batch, "Please enable QPair and connect with phone...", 100, 400);
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
    	commFun.unregisterListener(lsnr);

        Done();
	}

}
