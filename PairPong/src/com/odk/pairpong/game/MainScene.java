package com.odk.pairpong.game;

import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;

public class MainScene extends Scene {
	private ReceiverFunction rfunction;
    private SenderFunction sfunction;
	public MainScene(ReceiverFunction rfunction, SenderFunction sfunction){
		super();
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.sfunction.setpackage("com.odk.pairpongsender");
	}
	@Override
	public void firstPreparation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postRender() {
		// TODO Auto-generated method stub
		if(rfunction.getbool())
            SceneMgr.switchScene(new TestScene(rfunction, sfunction));
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
