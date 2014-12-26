package com.odk.pairpongsender.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.odk.pairpongsender.ControllerActivity;

class SenderRunnable implements Runnable {
    private Object obj;
    private boolean needToSend = false;
    private SenderFunction sfunction;
    private boolean stop = false;
    private Json json = new Json();
    
    public SenderRunnable (SenderFunction sfunction) {
        this.sfunction = sfunction;
    }

    @Override
    public void run () {
        while (!stop) {
            if (needToSend) {
                needToSend = false;
                sfunction.sendstring(json.toJson(obj));
                obj = null;
            } 

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                stop = true;
            }
        }
    }
    
    public void requestToStop () {
        stop = true;
    }
    
    public void pend(Object obj) {
        synchronized (this) {
            this.obj = obj;
            needToSend = true;
        }
    }
}

public class MainGame extends ApplicationAdapter {
    private Texture texBoard;
    private Texture texPoint;
    private Texture texRacket;
    private SenderFunction sfunction;
    private ReceiverFunction rfunction;
    private SpriteBatch spriteBatch;
    private GThetaProvider gThetaProvider = new GThetaProvider();
    private ControllerActivity ownerActivity;
    
    private int width, height;
    
    private Thread senderThread;
    private SenderRunnable senderRunnable;

    public MainGame (SenderFunction sfunction, ReceiverFunction rfunction, ControllerActivity ownerActivity) {
        this.sfunction = sfunction;
        this.rfunction = rfunction;
        this.ownerActivity = ownerActivity;
    }

    @Override
    public void create() {
        texBoard = new Texture("board.png");
        texPoint = new Texture("point.png");
        texRacket = new Texture("racket.png");
        spriteBatch = new SpriteBatch();
        senderRunnable = new SenderRunnable(sfunction);
        senderThread = new Thread(senderRunnable);
        senderThread.setDaemon(true);
        senderThread.start();
    }

    @Override
    public void dispose() {
        texBoard.dispose();
        texPoint.dispose();
        spriteBatch.dispose();
    }

    @Override
    public void resume() {
        senderRunnable = new SenderRunnable(sfunction);
        senderThread = new Thread(senderRunnable);
        senderThread.start();
    }

    @Override
    public void pause() {
        senderRunnable.requestToStop();
        try {
            senderThread.join();
        } catch (InterruptedException e) {
        }
    }
    
    private String lastUUID = "";
    private int loading=0;
    private Json json = new Json();
    private float posX = 0.45f;
    private float posY = 0.12f;
    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float theta = gThetaProvider.obtainTheta();

        // Input Polling
        if(loading<15){
        	loading++;
            sfunction.sendint(0);//���ھ� �Է� �Ϸ����� ����
        } else if (Gdx.input.isTouched()) {
            posY = 1 - Gdx.input.getY() / (float)height;
        	if(posY<0.35f)
        		posX = Gdx.input.getX() / (float)width;
            if(posX<0.17f)
                posX = 0.17f;
            if(posX>0.83f)
                posX=0.83f;
            if(posY>0.8f)
                posY=0.8f;
        }
        if(posY>0.12f)
            posY-=0.025f;
        if(posY<0.12f)
            posY=0.12f;
        senderRunnable.pend(new SenderInfo(posX, posY, theta));
        String infoString = rfunction.getstring();
        if (infoString != null && !infoString.equals("")) {
            ReceiverInfo receiverInfo = json.fromJson(ReceiverInfo.class, infoString);
            if (!receiverInfo.uuid.equals(lastUUID)) {
                Gdx.input.vibrate(receiverInfo.duration);
                lastUUID = receiverInfo.uuid;
            }
        }

        if (rfunction.getint()==7 || rfunction.getint()==1){//7�� ���ھ� ȭ������, 1�� ���� ȭ������ �Ѿ�ϴ�.
        	ownerActivity.quitNow();
        }
        // render by sprite batch
        spriteBatch.begin();
        spriteBatch.draw(texBoard, 0, 0, width, height);
        spriteBatch.draw(texRacket, width/10, (int)(posY * height - height/20), width*4/5, height/10);
        spriteBatch.draw(texPoint, (int)(posX * width - width/20), (int)(posY * height - height/30), width/10, height/15);
        spriteBatch.end();
    }


    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
