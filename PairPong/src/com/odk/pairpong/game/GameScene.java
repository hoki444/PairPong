package com.odk.pairpong.game;


import java.util.Random;

import com.algy.schedcore.IComp;
import com.algy.schedcore.frontend.ItemReservable;
import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;
import com.algy.schedcore.middleend.AssetModelComp;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.DirectionalLightComp;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.InputComp;
import com.algy.schedcore.middleend.ModelComp;
import com.algy.schedcore.middleend.PointLightComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtDebugDrawerComp;
import com.algy.schedcore.middleend.bullet.BtDetectorComp;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.algy.schedcore.middleend.bullet.CollisionComp;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Json;

class MyCollision extends CollisionComp {
	GameItem ballItem;
	Score score;
	Random random = new Random();
	MyCollision(GameItem ballItem, Score score){
		super();
		this.ballItem=ballItem;
		this.score=score;
	}
    
	@Override
	public IComp duplicate() {
		return new MyCollision(ballItem, score);
	}

	@Override
	public void beginCollision(GameItem other) {
		core().removeItem(other);
		GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
		newBall.as(BtRigidBodyComp.class).setLinearVelocity(
				new Vector3(2, random.nextFloat()*3-1.5f, random.nextFloat()*6-3f));
		newBall.setName("ball");
		core().addItem(newBall);
		score.resetCombo();
	}

	@Override
	public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
	}
}
class BackCollision extends CollisionComp {
	Score score;
	Random random = new Random();
	BackCollision(Score score){
		super();
		this.score=score;
	}
    
	@Override
	public IComp duplicate() {
		return new BackCollision(score);
	}

	@Override
	public void beginCollision(GameItem other) {
		if(score.getCombo()!=0){
			Vector3 tempv =new Vector3();
			other.getTransform().getTranslation(tempv);
			score.addAScore(tempv.y);
		}
	}

	@Override
	public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
	}
}
class Score{
	private int score;
	private int combo;
	private int stucktime;
	private int ascoretime;
	private int regentime;
	private float startspeed;
	private int scoretime;
	private int vscore;
	private int ascore;
	private int[] option;
	Score(int[] o){
		option = o;
		score=0;
		vscore=0;
		combo=0;
		stucktime=0;
		regentime=0;
		startspeed=0;
		scoretime=0;
	}
	public void setStartSpeed(float f){
		startspeed=f;
	}
	public void resetScore(){
		score=0;
	}
	public void resetCombo(){
		combo=0;
		stucktime=0;
		regentime=0;
	}
	public void addScore(int p){
		score+=p*(0.5+0.5*option[0]);
	}
	public int getScore(){
		return score;
	}
	public int getCombo(){
		return combo;
	}
	public void addCombo(){
		combo++;
	}
	public boolean reduceStuck(){
		if(stucktime!=0){
			stucktime--;
			if(stucktime==0)
				return true;
		}
		if(ascoretime!=0)
			ascoretime--;
		if(scoretime!=0)
			scoretime--;
		if(regentime!=0)
			regentime--;
		return false;
	}
	public void setStuck(){
		stucktime=30;
		scoretime=60;
		regentime=300;
	}
	public boolean isStuck(){
		return stucktime!=0;
	}
	public boolean needRegen(){
		return combo!=0 && regentime == 0;
	}
	public boolean showScore(){
		return scoretime!=0;
	}
	public boolean showAScore(){
		return ascoretime!=0;
	}
	public void addVScore(float endspeed){
		vscore=(int) ((startspeed+endspeed)*10*(0.5+0.5*option[0])*(1-0.2*option[1]));
		score+=vscore;
	}
	public void addAScore(float hity){
		ascore=(int) ((200-(1.3-hity)*(1.3-hity)*28)*(0.5+0.5*option[0])*(1.4-0.4*option[1]));
		score+=ascore;
		ascoretime=30;
	}
	public int getVscore(){
		return vscore;
	}
	public int getAscore(){
		return ascore;
	}
}

class VibCollision extends CollisionComp {
    private Json json = new Json();
    private SenderFunction sfunction;
    private Score score;
    int combo=0;
    public VibCollision (SenderFunction sfunction, Score score) {
        this.sfunction = sfunction;
        this.score=score;
    }

    @Override
    public IComp duplicate() {
        return new VibCollision(sfunction,score);
    }

    @Override
    public void beginCollision(GameItem other) {
        sfunction.sendstring(json.toJson(new ReceiverInfo(80)));
        if(!score.isStuck()){
        	score.addCombo();
        	combo=score.getCombo();
        	score.addScore((2*combo-1)*100);
        	score.setStuck();
        	score.setStartSpeed(other.as(BtRigidBodyComp.class).getLinearVelocity().len());
        }
    }

    @Override
    public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
    }
    
}

public class GameScene extends Scene {
	GameItem ballItem;
	GameItem racketItem ;
	int[] option;

    private ReceiverFunction rfunction;
    private SenderFunction sfunction;
	public GameScene (ReceiverFunction rfunction, SenderFunction sfunction, int[] o) {
		super();
		option=o;
		score= new Score(option);
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.sfunction.setpackage("com.odk.pairpongsender");
	}

    @Override
	public void reserveItem(Scene scene, ItemReservable coreProxy) { 
    	GameItem boardItembo = new GameItem(),
    			 debugdrawItem = new GameItem(new BtDebugDrawerComp()),
				 wallItem = new GameItem(),
                 lightItem = new GameItem(),
                 pointlightItem = new GameItem(new Transform(new Vector3(0, 1, 0)),
                		 						new PointLightComp(20).setColor(1, 1, 1, 1)),
                 removerItem = new GameItem();

        boardItembo.as(Transform.class).modify().setTranslation(0, 0, 0);
        boardItembo.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(2.f, .1f, 3.f)))
                      .setFriction(0.1f)
                      .setRestitution(0.98f));
        GameItem boardItemt = boardItembo.duplicate(new Vector3(0, 4.0f, 0));
        GameItem boardItemba = boardItembo.duplicate(new Vector3(2.1f, 2.0f, 0),
        		   new Quaternion(new Vector3(0,0,1), 90));
        boardItembo.add(new ModelComp(boxModelbo));
        boardItemba.add(new ModelComp(boxModelba));
        boardItemba.add(new BackCollision(score));
        boardItemt.add(new ModelComp(boxModelt));
       
        btCompoundShape racketCollShape = new btCompoundShape();
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(-.75f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.48f, .03f, .42f)));
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(.45f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.9f, .03f, .03f)));

        racketItem = new GameItem(new Transform(new Vector3(0, 2, 0),
			 new Quaternion(),
			 new Vector3(0.03f*(1.5f-0.5f*option[0]), 0.03f*(1.5f-0.5f*option[0]), 0.03f*(1.5f-0.5f*option[0]))));
        racketItem.add(BtRigidBodyComp
                      .kinematicBody(racketCollShape)
                      .setFriction(0.1f)
                      .activate()
                      .setRestitution(1.f));
        racketItem.setName("racket");
        
        wallItem.as(Transform.class).modify().setTranslation(0, 2.0f, 3.1f);
        wallItem.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(2.f, 2.f, .1f)))
                      .setFriction(0.1f)
                      .setRestitution(0.98f));
        GameItem wallItem2 = wallItem.duplicate(new Vector3(0, 2.0f, -3.1f));
        wallItem.add(new ModelComp(boxModels));
        wallItem2.add(new ModelComp(boxModels2));
        ballItem = new GameItem(new Transform(0, 0.2f, 0));
        ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.15f), 1)
                     .setAngularVelocity(new Vector3(0, 0, -10))
                     .setLinearVelocity(new Vector3(2, 1, 2))
                     .setRestitution(0.98f));
        ballItem.add(new ModelComp(ballModel));
        ballItem.setName("ball");
       
       
        racketItem.add(new AssetModelComp("racket.obj"));
        racketItem.add(new VibCollision(sfunction, score));
        racketItem.setName("racket");

        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.5f)).setColor(1.f, 1.f, 1.f, 1.0f));
        GameItem newBallItem = ballItem.duplicate(new Vector3(0, 2.8f, 0));
        newBallItem.setName("ball");
        coreProxy.reserveItem(newBallItem);
       
        removerItem.as(Transform.class).modify().setTranslation(0, -10f, 0);

        removerItem.add(new BtDetectorComp(new btBoxShape(new Vector3(50.f, 1f, 50.f))));
        removerItem.add(new MyCollision(ballItem,score));
        coreProxy.reserveItem(boardItembo);
        coreProxy.reserveItem(boardItemba);
        coreProxy.reserveItem(boardItemt);
        coreProxy.reserveItem(wallItem);
        coreProxy.reserveItem(wallItem2);
        coreProxy.reserveItem(lightItem);
        coreProxy.reserveItem(racketItem);
        coreProxy.reserveItem(debugdrawItem);
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0, 1, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0, 2, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(removerItem);
//        coreProxy.reserveItem(new GameItem(new SimpleCameraControllerComp()));
        coreProxy.reserveItem(new GameItem(new InputComp() {
            @Override
            public IComp duplicate() {
                return null;
            }
            
            @Override
            public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
                return false;
            }
            
            @Override
            public boolean touchDragged(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
                SceneMgr.switchScene(new GameScene(rfunction, sfunction,option));
                return false;
            }
            
            @Override
            public boolean scrolled(int arg0) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean mouseMoved(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean keyUp(int arg0) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean keyTyped(char arg0) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean keyDown(int arg0) {
                return false;
            }
        }));
        Done ();
	}

	@Override
	public void endResourceInitialization(Scene scene) {
        core.server(EnvServer.class).ambientLightColor.set(.4f, .4f, .4f, 1); 
        core.server(BtPhysicsWorld.class).world.setGravity(new Vector3(0, -9.8f, 0));
        
        core.server(CameraServer.class).setPosition(new Vector3(-4, 3f, 0))
                                       .lookAt(new Vector3(0, 2f, 0))
                                       .setUpVector(new Vector3(1, 0, 0))
                                       .setRange(1, 100);
        Done ();
	}

	private Model boxModelbo, boxModelba, boxModels, boxModels2, boxModelt, ballModel;
    private Texture tex, bottom, top, side, side2, back;
    Score score;
	BitmapFont bfont;
	Batch batch;
	int n=1800;
    private Json json = new Json();
    private String lastUUID = "";
    private StateInterpolater posXIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater posYIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater thetaIntp = new StateInterpolater(1, 90, 0, 900);
    
    @Override
    public void postRender() {
    	Random random = new Random();
        String infoString = rfunction.getstring();
        if (infoString != null && !infoString.equals("")) {
            SenderInfo newInfo = json.fromJson(SenderInfo.class, infoString);
            if (newInfo != null && !newInfo.uuid.equals(lastUUID)) {
                posXIntp.setDestState((newInfo.posX - 0.5f) * 6);
                posYIntp.setDestState(newInfo.posY * 4 + 0.2f);
                thetaIntp.setDestState(newInfo.theta * 1.5f);
                lastUUID = newInfo.uuid;
            }
        }
        if(score.needRegen()){
        	core.removeItem(core.getItemWithName("ball"));
        	GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
    		newBall.as(BtRigidBodyComp.class).setLinearVelocity(
    				new Vector3(2, random.nextFloat()*3-1.5f, random.nextFloat()*6-3f));
    		newBall.setName("ball");
    		core().addItem(newBall);
    		score.resetCombo();
        }
        if(score.reduceStuck()&&option[1]!=1){
        	score.addVScore(core.getItemWithName("ball").as(BtRigidBodyComp.class).getLinearVelocity().len());
        }
        if(n==0){
        	sfunction.sendint(7);//스마트폰 스코어 전환 시그널
            SceneMgr.switchScene(new ScoreScene(rfunction, sfunction,score.getScore()));
        }
        if(!rfunction.getbool()){//폰에서 종료시 종료
        	SceneMgr.switchScene(new MainScene(rfunction, sfunction));
        }
        else
        	n--;
    	batch.begin();
    	bfont.setColor(Color.WHITE);
        bfont.draw(batch, "Score : "+String.valueOf(score.getScore()), 0, 720);
        if(n<300)
        	bfont.setColor(Color.RED);
        bfont.draw(batch, String.valueOf(n/30), 600, 720);
        if(score.showScore()){
        	bfont.setColor(Color.RED);
        	bfont.draw(batch, String.valueOf(score.getCombo())+" Combo : "+
        	String.valueOf((int)((2*score.getCombo()-1)*100*(0.5+0.5*option[0]))), 0, 640);
        	if(!score.isStuck()&&option[1]!=1){
        		bfont.setColor(Color.GREEN);
        		bfont.draw(batch, " Velocity bonus : "+String.valueOf(score.getVscore()), 0, 560);
        	}
        }
        if(score.showAScore()){
        	bfont.setColor(Color.BLUE);
        	bfont.draw(batch, " Accuacy bonus : "+String.valueOf(score.getAscore()), 0, 480);
        }
        batch.end();
        posXIntp.update(0.03f);
        posYIntp.update(0.03f);
        thetaIntp.update(0.03f);
        racketItem.getTransform().modify().set(new Vector3(-2, 
                                                           posYIntp.getState(),
                                                           posXIntp.getState()),
                                               new Quaternion(new Vector3(0,0,1), 180f + thetaIntp.getState()),
                                               new Vector3(0.03f*(1.5f-0.5f*option[0]),
                                            		   0.03f*(1.5f-0.5f*option[0]),
                                            		   0.03f*(1.5f-0.5f*option[0])));
    }

    @Override
    public void firstPreparation() {
    	bfont= new BitmapFont();
    	batch = new SpriteBatch();
        bfont.setColor(Color.WHITE);
        bfont.scale(3f);
        bottom = new Texture("bottom.png");
        back = new Texture("back.png");
        top = new Texture("top.png");
        side = new Texture("side.png");
        side2 = new Texture("side2.png");
        boxModelbo = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(bottom))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelba = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(back))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelt = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(top))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels = new ModelBuilder().createBox(4, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(side))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels2 = new ModelBuilder().createBox(4, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(side2))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        ballModel = new ModelBuilder().createSphere(.3f, .3f, .3f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.95f, .95f, .95f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f)),
                Usage.Position | Usage.Normal); 
    }

    @Override
    public void tearDown() {
    	batch.dispose();
        bfont.dispose();
        boxModelbo.dispose();
        boxModelba.dispose();
        boxModels.dispose();
        Done ();
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
}