package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.odk.pairpong.comm.general.CommFunction;

public class MainScene extends Scene {
	private BitmapFont bfont;
	private SpriteBatch batch;
    private CommFunction commFun;
    
    private GameStartListener lsnr;
	public MainScene(CommFunction commFun) {
		super();
	    this.commFun = commFun;
	    this.lsnr = new GameStartListener(commFun);
	}

	@Override
	public void firstPreparation() {
    	bfont = new BitmapFont();
    	batch = new SpriteBatch();
    	
    	commFun.registerListener(lsnr);
        bfont.scale(3f);
	}
	
	@Override
	public void postRender() {
		batch.begin();
        bfont.setColor(Color.WHITE); 
        bfont.draw(batch, "Start Game in the Smartphone App", 200, 400);
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
