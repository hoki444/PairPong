package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;

public class ScoreScene extends Scene {
	private ReceiverFunction rfunction;
    private SenderFunction sfunction;
    private int score;
	public ScoreScene(ReceiverFunction rfunction, SenderFunction sfunction, int score){
		super();
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.score=score;
		this.sfunction.setpackage("com.odk.pairpongsender");
	}
	@Override
	public void firstPreparation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postRender() {
		// TODO Auto-generated method stub
		if(!rfunction.getbool())
			sfunction.sendint(score);
		else
			sfunction.sendint(7);
		if(rfunction.getint()==1){
            SceneMgr.switchScene(new MainScene(rfunction, sfunction));
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

}
