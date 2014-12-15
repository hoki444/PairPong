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
    private SenderFunction sfunction;
    private ReceiverFunction rfunction;
    private SpriteBatch spriteBatch;
    private GThetaProvider gThetaProvider = new GThetaProvider();
    private ControllerActivity nowactivity;
    
    private int width, height;
    
    private Thread senderThread;
    private SenderRunnable senderRunnable;

    public MainGame (SenderFunction sfunction, ReceiverFunction rfunction, ControllerActivity nowactivity) {
        this.sfunction = sfunction;
        this.rfunction = rfunction;
        this.nowactivity = nowactivity;
    }

    @Override
    public void create() {
        texBoard = new Texture("board.png");
        texPoint = new Texture("point.png");
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
    private float posY = 0.45f;
    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float theta = gThetaProvider.obtainTheta();

        boolean isTouched = false;
        // Input Polling
        if (Gdx.input.isTouched()) {
            isTouched = true;
            posX = Gdx.input.getX() / (float)width;
            posY = 1 - Gdx.input.getY() / (float)height;
            if(posX<0.055f)
                posX = 0.055f;
            if(posX>0.85f)
                posX=0.85f;
            if(posY<0.005f)
                posY=0.005f;
            if(posY>0.9f)
                posY=0.9f;
            senderRunnable.pend(new SenderInfo(posX, posY, theta));
        }
        
        String infoString = rfunction.getstring();
        if (infoString != null && !infoString.equals("")) {
            ReceiverInfo receiverInfo = json.fromJson(ReceiverInfo.class, infoString);
            if (!receiverInfo.uuid.equals(lastUUID)) {
                Gdx.input.vibrate(receiverInfo.duration);
                lastUUID = receiverInfo.uuid;
            }
        }
        if(loading<15){
        	loading++;
            sfunction.sendint(0);
        }
        if (rfunction.getint()==7){
        	nowactivity.myDestroy();
        }
        // render by sprite batch
        spriteBatch.begin();
        spriteBatch.draw(texBoard, 0, 0, width, height);
        spriteBatch.draw(texPoint, (int)(posX * width - 100), (int)(posY * height - 100), 200, 200);
        spriteBatch.end();
    }


    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
