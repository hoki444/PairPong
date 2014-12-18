package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainScene extends Scene {
	Option option;
	BitmapFont bfont;
	SpriteBatch batch;
	private ReceiverFunction rfunction;
    private SenderFunction sfunction;
	public MainScene(ReceiverFunction rfunction, SenderFunction sfunction){
		super();
		option=new Option();
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.sfunction.setpackage("com.odk.pairpongsender");
	}
	@Override
	public void firstPreparation() {
		// TODO Auto-generated method stub
    	bfont= new BitmapFont();
    	batch = new SpriteBatch();
        bfont.setColor(Color.WHITE); bfont.scale(3f);
		
	}

	@Override
	public void postRender() {
		// TODO Auto-generated method stub
		option.setData(rfunction.getintarray());
		batch.begin();
    	bfont.setColor(Color.WHITE);
        bfont.draw(batch, "Start Game in the Smartphone App", 200, 400);
        batch.end();
		if(rfunction.getbool()){
            SceneMgr.switchScene(new GameScene(rfunction, sfunction, option));
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tearDown(){
    	batch.dispose();
        bfont.dispose();
        
        Done();
	}

}
