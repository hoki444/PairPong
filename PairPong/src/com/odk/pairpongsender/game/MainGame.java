package com.odk.pairpongsender.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.odk.pairpong.comm.game.CommConstants;
import com.odk.pairpong.comm.game.CommRacketCollision;
import com.odk.pairpong.comm.game.CommRacketMoveCmd;
import com.odk.pairpong.comm.game.CommScore;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageCallback;
import com.odk.pairpong.comm.general.MessageListener;
import com.odk.pairpongsender.ControllerActivity;

class SenderRunnable implements Runnable {
    private Object obj;
    private boolean needToSend = false;
    private CommFunction commFun;
    private boolean stop = false;
    
    public SenderRunnable (CommFunction commFun) {
        this.commFun = commFun;
    }

    boolean responsed = false;
    @Override
    public void run () {
        while (!stop) {
            if (needToSend && obj != null) {
                needToSend = false;
                responsed = false;
                commFun.sendMessage(CommConstants.TYPE_RACKET_MOVE_COMMAND, obj, 
                    new MessageCallback() {
                        @Override
                        public void onSuccess() {
                            responsed = true;
                        }
                        
                        @Override
                        public void onError(String reason) {
                            responsed = true;
                        }
                });
                while (!stop && !responsed) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        stop = true;
                        break;
                    }
                }
                obj = null;
            } 
        }
    }
    
    public void requestToStop () {
        stop = true;
    }
    
    public void pend(Object obj) {
        if (obj != null) {
            synchronized (this) {
                this.obj = obj;
                needToSend = true;
            }
        }
    }
}

public class MainGame extends ApplicationAdapter {
    private Texture texBoard;
    private Texture texArrow;
    private Texture texPoint;
    private Texture texRacket;
    private CommFunction commFun;
    private SpriteBatch spriteBatch;
    private GThetaProvider gThetaProvider = new GThetaProvider();
    private ControllerActivity ownerActivity;
    
    private int width, height;
    
    private Thread senderThread;
    private SenderRunnable senderRunnable;
    
    private MessageListener<CommRacketCollision> racketCollListener = new MessageListener<CommRacketCollision>() {
        @Override
        public void onReceive(CommRacketCollision obj) {
            Gdx.input.vibrate(obj.duration);
        }
        
        @Override
        public String getTypeName() {
            return CommConstants.TYPE_RACKET_COLLISION;
        }
        
        @Override
        public Class<CommRacketCollision> getTypeClass() {
            return CommRacketCollision.class;
        }
    };
    
    private MessageListener<CommScore> scoreListener = new MessageListener<CommScore>() {
        @Override
        public String getTypeName() {
            return CommConstants.TYPE_SCORE;
        }

        @Override
        public Class<CommScore> getTypeClass() {
            return CommScore.class;
        }

        @Override
        public void onReceive(CommScore obj) {
            ownerActivity.quitWithScore(obj.score);
        }
    };

    public MainGame (CommFunction commFun, ControllerActivity ownerActivity) {
        this.commFun = commFun;
        this.ownerActivity = ownerActivity;
    }

    @Override
    public void create() {
        texBoard = new Texture("board.png");
        texPoint = new Texture("point.png");
        texRacket = new Texture("racket.png");
        texArrow = new Texture("smasharrow.png");
        spriteBatch = new SpriteBatch();
        senderRunnable = new SenderRunnable(commFun);
        senderThread = new Thread(senderRunnable);
        senderThread.setDaemon(true);
        senderThread.start();
        
        /* register listeners */
        commFun.registerListener(racketCollListener);
        commFun.registerListener(scoreListener);
        
        isConnected = commFun.isConnected();
    }

    @Override
    public void dispose() {
        texBoard.dispose();
        texPoint.dispose();
        spriteBatch.dispose();

        /* unregister listeners */
        commFun.unregisterListener(racketCollListener);
        commFun.unregisterListener(scoreListener);
    }

    @Override
    public void resume() {
        senderRunnable = new SenderRunnable(commFun);
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
    
    private float posX = 0.45f;
    private float posY = 0.12f;
    
    
    private int frameCount = 0;
    private boolean isConnected;
    
    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        if (frameCount >= 10) {
            isConnected = commFun.isConnected();
            frameCount = 0;
        } else
            frameCount++;
        
        if (!isConnected) {
            ownerActivity.quit("The connection has been lost unexpectedly.");
        }

        float theta = gThetaProvider.obtainTheta();

        // Input Polling
        boolean slidingDown = false;
        if (Gdx.input.isTouched()) { //  && status == TouchStatus.Bottom) {
            posX = Gdx.input.getX() / (float)width;
            posY = 1 - Gdx.input.getY() / (float)height;

        } else if(posY > 0.12f) {
            slidingDown = true;
            posY -= 0.05f;
        } 

        if (posY <= 0.12f) {
            posY = 0.12f;
        }
        if(posX <= 0.17f)
            posX = 0.17f;
        else if (posX >= 0.83f)
            posX = 0.83f;

        senderRunnable.pend(new CommRacketMoveCmd(posX, posY, theta));

        // render by sprite batch
        spriteBatch.begin();
        spriteBatch.draw(texBoard, 0, 0, width, height);
        spriteBatch.draw(texArrow, 0.3f*width, 0.12f*height, 0.4f*width, 0.68f*height);
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
