package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScoreScene extends Scene {
	private ReceiverFunction rfunction;
    private SenderFunction sfunction;
    private ServiceFunction service;
    private int score;
	BitmapFont bfont;
	SpriteBatch batch;
	public ScoreScene(ReceiverFunction rfunction, SenderFunction sfunction, int score, ServiceFunction service){
		super();
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.service = service;
		this.score=score;
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
		batch.begin();
    	bfont.setColor(Color.WHITE);
        bfont.draw(batch, "Sending Score......", 400, 400);
        batch.end();
		if(!service.isstartstate())
			sfunction.sendint(score);
		else
			sfunction.sendint(7);//스코어화면 전환요청
		if(rfunction.getint()==1){//스코어 전송완료신호
            SceneMgr.switchScene(new MainScene(rfunction, sfunction, service));
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
