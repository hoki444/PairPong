package com.odk.pairpong.game;


import java.util.Random;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.GameItem;
import com.algy.schedcore.SchedTask;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.TaskController;
import com.algy.schedcore.frontend.ItemReservable;
import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.frontend.SceneMgr;
import com.algy.schedcore.middleend.AssetModelComp;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.DirectionalLightComp;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.ModelComp;
import com.algy.schedcore.middleend.PointLightComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtDetectorComp;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.algy.schedcore.middleend.bullet.CollisionComp;
import com.algy.schedcore.middleend.bullet.CollisionFilter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.odk.pairpong.comm.game.CommConstants;
import com.odk.pairpong.comm.game.CommOption;
import com.odk.pairpong.comm.game.CommRacketCollision;
import com.odk.pairpong.comm.game.CommRacketMoveCmd;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageListener;
//import com.algy.schedcore.middleend.bullet.BtDebugDrawerComp;

class MyCollision extends CollisionComp {
	GameItem ballItem;
	Score score;
	Random random = new Random();
	public MyCollision(GameItem ballItem, Score score){
		super();
		this.ballItem=ballItem;
		this.score=score;
	}
    
	@Override
	public BaseComp duplicate() {
		return new MyCollision(ballItem, score);
	}

	@Override 
	public void beginCollision(GameItem other) {
	    if (other.getItemType().isTypeOf(BallType.class)) {
            core().removeItem(other);
            GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
            newBall.as(BtRigidBodyComp.class).setLinearVelocity(
                    new Vector3(6, 2, random.nextFloat()*3-1.5f));
            core().addItem(newBall);
            score.resetCombo();
            score.setLife(score.getLife()-1);
	    }
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
	public BaseComp duplicate() {
		return new BackCollision(score);
	}

	@Override
	public void beginCollision(GameItem other) {
		if(score.getCombo()!=0){
			score.addAScore();
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
	private int life;
	private CommOption option;
	public Score(CommOption o){
		life = 2250;
		option = o;
		score =0;
		vscore = 0;
		combo = 0;
		stucktime = 0;
		regentime = 0;
		startspeed = 0;
		scoretime = 0;
	}
	public void timePass(){
		life--;
	}
	public void setLife(int l){
		if(option.gameMode!=0)
			life=l;
	}
	public int getLife(){
		return life;
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
		score+=p*(0.5+0.5*option.racketSize)*(1+0.1*option.gameMode);
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
		vscore=(int) ((startspeed+endspeed)*10*(0.5+0.5*option.racketSize)
				*(1+0.1*option.gameMode))*combo;
		score+=vscore;
	}
	public void addAScore(){
		if(isStuck())
			stucktime=1;
	}
	public int getVscore(){
		return vscore;
	}
}

class RacketCollision extends CollisionComp {
    private GameScene parent;
    private CommFunction commFun;
    private Score score;
    private EffectController effectController;
    int combo=0;

    public RacketCollision (GameScene parent, CommFunction commFun, Score score, EffectController effectController) {
        this.parent = parent;
        this.commFun = commFun;
        this.score = score;
        this.effectController = effectController;
    }

    @Override
    public BaseComp duplicate() {
        return new RacketCollision(parent, commFun, score, effectController);
    }

    @Override
    public void beginCollision(GameItem other) {
        boolean isSmashing = parent.isSmashing;
        CommRacketCollision collInfo = new CommRacketCollision(isSmashing?200:80);
        collInfo.isSmashing = isSmashing;

        commFun.sendMessage(CommConstants.TYPE_RACKET_COLLISION, collInfo, null);
        if (other.getItemType().isTypeOf(BallType.class) && !other.as(BallStateComp.class).isRollingOnGround) {
            if(!score.isStuck()) {
                score.addCombo();
                combo = score.getCombo();
                score.addScore((2*combo-1)*100);
                score.setStuck();
                score.setStartSpeed(other.as(BtRigidBodyComp.class).getLinearVelocity().len());
            }
        }
    }

    @Override
    public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
        Vector3 position = new Vector3();
        info.iterator().next().otherPosition(position);
        effectController.invokeHit(position);
    }
}

class BallCollision extends CollisionComp {
    public Sound wallSound;
    public Sound racketSound;
    
    public BallCollision (Sound wallSound, Sound racketSound) {
        this.wallSound = wallSound;
        this.racketSound = racketSound;
    }

    @Override
    public BaseComp duplicate() {
        return new BallCollision(wallSound, racketSound);
    }

    @Override
    public void beginCollision(GameItem other) {
        if (other.getItemType().isTypeOf(RacketType.class)) {
            if (racketSound != null) {
                racketSound.play();
            }
        } else {
            // wishing something collided with me must be wall...
            if (wallSound != null) {
                wallSound.play();
            }
            
            if (other.getItemType().isTypeOf(GroundType.class)) {
                item().as(BallStateComp.class).isRollingOnGround = true;
            }
        }
    }

    @Override
    public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
        if (other.getItemType().isTypeOf(GroundType.class)) {
            item().as(BallStateComp.class).isRollingOnGround = false;
        }
    }
    
    
}

public class GameScene extends Scene {
    public static short GROUP_WALL = 1;
    public static short GROUP_BALL = 2;
    public static short GROUP_RACKET = 4;
    public static short GROUP_DETECTOR = 8;

	private GameItem ballItem;
	private GameItem racketItem;
	private CommOption option;

    private CommFunction commFun;
    
    private Sound wallSound;
    private Sound racketSound;
    private Music bgroundSound;
    
    private CeaseGameListener ceaseGameListener;

	public GameScene (CommFunction commFun, CommOption o) {
		super();
		option = o;
		score = new Score(option);
		this.commFun = commFun;
		
		if(option.gameMode == 1)
			score.setLife(5);
		if(option.gameMode == 2)
			score.setLife(1);
	}

    @Override
	public void reserveItem(Scene scene, ItemReservable coreProxy) {
    	float Frictions=0.05f;
    	float Restitutions=0.98f;
    
    	GameItem boardItembo = new GameItem(),
    			 //debugdrawItem = new GameItem(new BtDebugDrawerComp()),
				 wallItem = new GameItem(),
                 lightItem = new GameItem(),
                 removerItem = new GameItem();

        boardItembo.as(Transform.class).modify().setTranslation(1f, 0, 0);
        GameItem boardItemba = boardItembo.duplicate(new Vector3(4.1f, 2.0f, 0),
     		   new Quaternion(new Vector3(0,0,1), 90));
        boardItembo.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(3.f, .2f, 3.f)), new CollisionFilter(GROUP_WALL, GROUP_BALL))
                      .setFriction(Frictions)
                      .setRestitution(1f));
        boardItemba.add(BtRigidBodyComp
                .staticBody(new btBoxShape(new Vector3(3.f, .2f, 3.f)), new CollisionFilter(GROUP_WALL, GROUP_BALL))
                .setFriction(Frictions)
                .setRestitution(0.7f));

        // Tunneling-proof 
        btRigidBody backBody = boardItemba.as(BtRigidBodyComp.class).getRigidBody();
        backBody.setCcdMotionThreshold(1e-6f);
        backBody.setCcdSweptSphereRadius(4f);

        GameItem boardItemt = boardItembo.duplicate(new Vector3(1f, 4.0f, 0));
        boardItembo.add(new ModelComp(boxModelbo));
        boardItembo.setItemType(new GroundType());
        boardItemba.add(new ModelComp(boxModelba));
        boardItemba.add(new BackCollision(score));
        boardItemt.add(new ModelComp(boxModelt));
       
        btCompoundShape racketCollShape = new btCompoundShape();
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(-.75f*(1.5f-0.35f*option.racketSize), 
        		.03f*(1.5f-0.35f*option.racketSize), .03f*(1.5f-0.35f*option.racketSize)), new Quaternion()),
        		new btBoxShape(new Vector3(.48f*(1.5f-0.35f*option.racketSize), .1f*(1.5f-0.35f*option.racketSize), 
        				.42f*(1.5f-0.35f*option.racketSize))));
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(.45f*(1.5f-0.35f*option.racketSize),
        		.03f*(1.5f-0.35f*option.racketSize), .03f*(1.5f-0.35f*option.racketSize)), new Quaternion()),
        		new btBoxShape(new Vector3(.9f*(1.5f-0.35f*option.racketSize), .1f*(1.5f-0.35f*option.racketSize),
        				.1f*(1.5f-0.35f*option.racketSize))));

        racketItem = new GameItem(new Transform(new Vector3(-2, 2.2f, 0),
			 new Quaternion(new Vector3(0, 0, -1), 90),
			 new Vector3(0.03f*(1.5f-0.35f*option.racketSize), 0.03f*(1.5f-0.35f*option.racketSize),
					 0.03f*(1.5f-0.35f*option.racketSize))));
        racketItem.add(BtRigidBodyComp
                      .dynamicBody(racketCollShape, 1000, new CollisionFilter(GROUP_RACKET, GROUP_BALL))
                      .setFriction(Frictions)
                      .activate()
                      .setRestitution(Restitutions)
                      .forceGravity(new Vector3()));
        racketItem.setItemType(new RacketType());
        // Tunneling-proof 
        btRigidBody racketBody = racketItem.as(BtRigidBodyComp.class).getRigidBody();
        racketBody.setCcdMotionThreshold(1e-6f);
        racketBody.setCcdSweptSphereRadius(4f);
        
        
        wallItem.as(Transform.class).modify().setTranslation(1f, 2.0f, 3.1f);
        wallItem.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(3.f, 2.f, .1f)), new CollisionFilter(GROUP_WALL, GROUP_BALL))
                      .setFriction(Frictions)
                      .setRestitution(1f));
        GameItem wallItem2 = wallItem.duplicate(new Vector3(1f, 2.0f, -3.1f));
        wallItem.add(new ModelComp(boxModels));
        wallItem2.add(new ModelComp(boxModels2));
        ballItem = new GameItem(new Transform(0, 0.2f, 0));
        ballItem.add(new BallCollision(wallSound, racketSound));
        ballItem.add(new BallStateComp(false));
        ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.15f), 1, 
                                                 new CollisionFilter(GROUP_BALL, 
                                                                     (short)(GROUP_DETECTOR | GROUP_WALL | GROUP_RACKET)))
                     .setAngularVelocity(new Vector3(0, 0, -10))
                     .setLinearVelocity(new Vector3(6, 2, 1))
                     .setRestitution(0.9f)
                     .setLinearDamping(0f));
        ballItem.add(new ModelComp(ballModel));
        ballItem.setItemType(new BallType());
        

        // Tunneling-proof 
        btRigidBody ballBody = ballItem.as(BtRigidBodyComp.class).getRigidBody();
        ballBody.setCcdMotionThreshold(1e-6f);
        ballBody.setCcdSweptSphereRadius(4f);
       
        racketItem.add(new AssetModelComp("racket.obj"));
        racketItem.add(new RacketCollision(this, commFun, score,
                new EffectController() {
                    @Override
                    public void invokeHit(Vector3 position) {
                        Vector3 screen = core().getCompMgr(CameraServer.class).getCamera().project(position);
                        hitEffect.setPosition(new Vector2(screen.x, screen.y), new Vector2(screen.x, screen.y + 30));
                        hitEffect.rewind();
                    }
        }));
        racketItem.setItemType(new RacketType());

        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -20f, 10f)).setColor(1.f, 1.f, 1.f, 1.0f));
        GameItem newBallItem = ballItem.duplicate(new Vector3(0, 2.8f, 0));
        coreProxy.reserveItem(newBallItem);
       
        removerItem.as(Transform.class).modify().setTranslation(0, -10f, 0);

        removerItem.add(new BtDetectorComp(new btBoxShape(new Vector3(50.f, 1f, 50.f)), new CollisionFilter(GROUP_DETECTOR, (short)-1)));
        removerItem.add(new MyCollision(ballItem,score));
        coreProxy.reserveItem(boardItembo);
        coreProxy.reserveItem(boardItemba);
        coreProxy.reserveItem(boardItemt);
        coreProxy.reserveItem(wallItem);
        coreProxy.reserveItem(wallItem2);
        coreProxy.reserveItem(lightItem);
        coreProxy.reserveItem(racketItem);
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0.9f, 1, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0, 2, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(removerItem);
        Done ();
	}

	@Override
	public void endResourceInitialization(Scene scene) {
        core.getCompMgr(EnvServer.class).ambientLightColor.set(.7f, .7f, .7f, 1); 
        core.getCompMgr(BtPhysicsWorld.class).world.setGravity(new Vector3(0, -9.8f, 0));
        
        core.getCompMgr(CameraServer.class).setPosition(new Vector3(-4, 3f, 0))
                                       .lookAt(new Vector3(0, 2f, 0))
                                       .setUpVector(new Vector3(1, 0, 0))
                                       .setRange(1, 100);

        if(option.soundMode%2==0)
        	bgroundSound.play();
        /*
         * schedule periodic jobs
         */
        schedule(100, 400, new SchedTask() {
            @Override
            public void onScheduled(SchedTime time) {
                if (!commFun.isConnected()) {
                    SceneMgr.switchScene(new MainScene(commFun));
                }
            }
            
            @Override
            public void endSchedule(TaskController t) {
            }
            
            @Override
            public void beginSchedule(TaskController t) {
            }
        });
        schedule(0, 32, new SchedTask() {
            Random random = new Random();
            @Override
            public void onScheduled(SchedTime time) {
                if (score.needRegen()) {
                    core.removeItem(core.firstItemWithType(BallType.class));
                    GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
                    newBall.as(BtRigidBodyComp.class).setLinearVelocity(
                            new Vector3(6, 2, random.nextFloat()*3-1.5f));
                    core().addItem(newBall);
                    score.resetCombo();
                    score.setLife(score.getLife()-1);
                }
                
                if(score.reduceStuck()) {
                    score.addVScore(core.firstItemWithType(BallType.class).as(BtRigidBodyComp.class).getLinearVelocity().len());
                }

                if(score.getLife() == 0) {
                    SceneMgr.switchScene(new ScoreScene(commFun, score.getScore()));
                }

                if(option.gameMode == 0)
                    score.timePass();
                else{
                	musictime++;
                	if(musictime>2190&&option.gameMode!=0){
                		musictime=0;
                		if(option.soundMode%2==0){
                			bgroundSound.stop();
                			bgroundSound.play();
                		}
                	}
                }
            }
            
            @Override
            public void endSchedule(TaskController t) {
            }
            
            @Override
            public void beginSchedule(TaskController t) {
            }
        });
        
        schedule(0, 32, new SchedTask() {
            @Override
            public void onScheduled(SchedTime time) {
                BtRigidBodyComp bodyComp = racketItem.as(BtRigidBodyComp.class);
                Vector3 racketTr = racketItem.getTransform().getTranslation(new Vector3());

                Quaternion racketOri = new Quaternion();
                bodyComp.getRigidBody().getWorldTransform().getRotation(racketOri, true);

                float destTheta;
                synchronized (lockRacketIntpt) {
                    posXIntp.setState(racketTr.z);
                    nowY=core.firstItemWithType(BallType.class).getTransform().getTranslation(new Vector3()).y-0.75f*(1.5f-0.35f*option.racketSize);
                    if(nowY<0)
                    	nowY=0;
                    if(nowY>4)
                    	nowY=4;
                    posYIntp.setDestState(nowY);
                    posYIntp.setState(racketTr.y);
                    destTheta = (90 + rawTheta);
                }
                
                float scale;
                Vector3 axis = new Vector3();
                
                float theta = new Quaternion(racketOri).conjugate()
                              .mul(new Quaternion(new Vector3(0, 0, -1), destTheta))
                              .getAxisAngle(axis);
                axis.x = 0;
                axis.y = 0;
                axis = axis.nor();
                if (theta > 180) {
                    theta -= 360;
                }

                if (theta <= 10 && theta >= -10) {
                    scale = 0;
                } else if (theta > 0) {
                    scale = !isSmashing? RACKET_SPEED:RACKET_SMASH_SPEED;
                } else
                    scale = !isSmashing?-RACKET_SPEED:-RACKET_SMASH_SPEED;

                bodyComp.activate();
                synchronized (lockRacketIntpt) {
                    bodyComp.setLinearVelocity(new Vector3(0, posYIntp.getVelocity(), posXIntp.getVelocity()));
                }
                bodyComp.setAngularVelocity(axis.scl(scale));
            }
            
            @Override
            public void endSchedule(TaskController t) {
            }
            
            @Override
            public void beginSchedule(TaskController t) {
            }
        });

        Done ();
	}

	private Model boxModelbo, boxModelba, boxModels, boxModels2, boxModelt, ballModel;
    private Texture bottom, top, side, side2, back, ballTex;
    private Texture hitTex;
    private TextureRegion hitTexReg;
    private HUDSpriteEffect hitEffect;
    
    Score score;
	BitmapFont bfont;
	SpriteBatch batch;
	float nowY = 0;
	int musictime=0;
    private StateInterpolater posXIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater posYIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    float rawTheta = 0;
    static final int RACKET_SPEED = 3;
    static final int RACKET_SMASH_SPEED = 4;
    
    boolean isSmashing = false;
    
    private Object lockRacketIntpt = new Object();
    private MessageListener<CommRacketMoveCmd> racketMoveLisnr = new MessageListener<CommRacketMoveCmd>() {
        @Override
        public void onReceive(CommRacketMoveCmd obj) {
            synchronized (lockRacketIntpt) {
            	posXIntp.setDestState((obj.posX - 0.5f) * 8f);
            	
            	if(obj.posY > 0.35f) {
            	    // smash state
                    isSmashing = true;
            	    rawTheta = 70;
            	} else {
                    isSmashing = false;
            		rawTheta = 90 * obj.posY / 0.35f - 30;
            	}
            }
        }
        
        @Override
        public String getTypeName() {
            return CommConstants.TYPE_RACKET_MOVE_COMMAND;
        }
        
        @Override
        public Class<CommRacketMoveCmd> getTypeClass() {
            return CommRacketMoveCmd.class;
        }
    };

    @Override
    public void postRender() {
    	batch.begin();
    	bfont.setColor(Color.WHITE);
        bfont.draw(batch, "Score : "+String.valueOf(score.getScore()), 0, 720);
        if(score.getScore()>option.highScores[0]){
        	bfont.draw(batch, "1st Score!!", 800, 720);
        }
        for (int i=0;i<4;i++) {
        	if(score.getScore()<option.highScores[i]&&score.getScore()>option.highScores[i+1]){
        		bfont.draw(batch, String.valueOf(i+1)+"th Score : "+String.valueOf(option.highScores[i]),
        				800, 720);
        		bfont.draw(batch, "last : "+String.valueOf(option.highScores[i]-score.getScore()), 800, 640);
        	}
        }
        if(score.getScore()<option.highScores[4]){
        	bfont.draw(batch, "5th Score : "+String.valueOf(option.highScores[4]),
    				800, 720);
    		bfont.draw(batch, "last : "+String.valueOf(option.highScores[4]-score.getScore()), 800, 640);
        }

        if((score.getLife()<300&&option.gameMode==0)||score.getLife()<2)
        	bfont.setColor(Color.RED);
        if(option.gameMode==0)
        	bfont.draw(batch, String.valueOf(score.getLife()/30), 600, 720);
        else
        	bfont.draw(batch, "last ball : "+String.valueOf(score.getLife()-1), 450, 720);

        if(score.showScore()) {
        	bfont.setColor(Color.RED);
        	if(score.getCombo()!=0)
        		bfont.draw(batch, String.valueOf(score.getCombo())+" Combo : "+
        				String.valueOf((int)((2*score.getCombo()-1)*100*(0.5+0.5*option.racketSize)*(1+0.1*option.gameMode))), 0, 640);
        	if(!score.isStuck()){
        		bfont.setColor(Color.GREEN);
        		bfont.draw(batch, " Speed bonus : "+String.valueOf(score.getVscore()), 0, 560);
        	}
        }

        // HUD sprites
        if (hitEffect.isRunning()) {
            hitEffect.render(batch);
            hitEffect.advance(Gdx.graphics.getDeltaTime());
        } 
        batch.end();
    }

    @Override
    public void prepare() {
        /*
         * Register listeners
         */
        ceaseGameListener = new CeaseGameListener(commFun);
        commFun.registerListener(ceaseGameListener);
        commFun.registerListener(racketMoveLisnr);

        
    	batch = new SpriteBatch();
    	bfont = new BitmapFont(Gdx.files.internal("yuppy_tc_45.fnt"));
        bfont.setColor(Color.WHITE); 
        bottom = new Texture("bottom.png");
        back = new Texture("back.png");
        top = new Texture("top.png");
        side = new Texture("side.png");
        side2 = new Texture("side2.png");
        ballTex = new Texture("ball.png");
        hitTex  = new Texture("Effect_hit.png");
        hitTexReg = new TextureRegion(hitTex);
        
        hitEffect = new HUDSpriteEffect(hitTexReg, 0.5f, new Vector2())
                    .setScale(0.2f, 0.2f)
                    .setAlpha(1.f, 0f);

        if(option.soundMode<2){
        	wallSound = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
        	racketSound = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
        }
        if(option.specialMode==2)
        	bgroundSound = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        else
        	bgroundSound = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        bgroundSound.setLooping(true);

        boxModelbo = new ModelBuilder().createBox(6, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(bottom))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelba = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(back))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelt = new ModelBuilder().createBox(6, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(top))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels = new ModelBuilder().createBox(6, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(side))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels2 = new ModelBuilder().createBox(6, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(side2))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        ballModel = new ModelBuilder().createSphere(.3f, .3f, .3f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.25f, .25f, .25f, 1f),
                             ColorAttribute.createAmbient(0.5f, 0.5f, 0.5f, 1.f),
                             TextureAttribute.createDiffuse(new TextureRegion(ballTex))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates); 
    }

    @Override
    public void tearDown() {
        /*
         * Unregister listeners
         */
        commFun.unregisterListener(ceaseGameListener);
        commFun.unregisterListener(racketMoveLisnr);
        
        
    	batch.dispose();
        bfont.dispose();
        boxModelbo.dispose();
        boxModelba.dispose();
        boxModels.dispose();
        boxModels2.dispose();
        boxModelt.dispose();
        ballModel.dispose();

        bottom.dispose();
        top.dispose();
        side.dispose();
        side2.dispose();
        back.dispose();
        ballTex.dispose();
        hitTex.dispose();

        if (wallSound != null)
            wallSound.dispose();
        if (racketSound != null)
        	racketSound.dispose();
        if (bgroundSound != null) {
            bgroundSound.stop();
        	bgroundSound.dispose();
        }
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
