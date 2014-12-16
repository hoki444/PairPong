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
import com.algy.schedcore.middleend.ModelComp;
import com.algy.schedcore.middleend.PointLightComp;
import com.algy.schedcore.middleend.SimpleCameraControllerComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtDebugDrawerComp;
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
import com.badlogic.gdx.math.Vector2;
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
	    if ("ball".equals(other.getName())) {
		core().removeItem(other);
		GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
		newBall.as(BtRigidBodyComp.class).setLinearVelocity(
				new Vector3(6, 2, random.nextFloat()*3-1.5f));
		newBall.setName("ball");
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
	private int life;
	private Option option;
	Score(Option o){
		life=1950;
		option = o;
		score=0;
		vscore=0;
		combo=0;
		stucktime=0;
		regentime=0;
		startspeed=0;
		scoretime=0;
	}
	public void timePass(){
		life--;
	}
	public void setLife(int l){
		if(option.gamemode!=0)
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
		score+=p*(0.5+0.5*option.racketsize)*(1+0.1*option.gamemode);
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
		vscore=(int) ((startspeed+endspeed)*10*(0.5+0.5*option.racketsize)
				*(1-0.2*option.scoremode)*(1+0.1*option.gamemode));
		score+=vscore;
	}
	public void addAScore(float hity){
		if(option.scoremode!=0){
			ascore=(int) ((200-(1.33-hity)*(1.33-hity)*122)*(0.5+0.5*option.racketsize)
					*(1.4-0.4*option.scoremode)*(1+0.1*option.gamemode));
			if(ascore>0){
				score+=ascore;
				ascoretime=30;
			}
		}
	}
	public int getVscore(){
		return vscore;
	}
	public int getAscore(){
		return ascore;
	}
}

class RacketCollision extends CollisionComp {
    private Json json = new Json();
    private SenderFunction sfunction;
    private Score score;
    private EffectController effectController;
    int combo=0;
    public RacketCollision (SenderFunction sfunction, Score score, EffectController effectController) {
        this.sfunction = sfunction;
        this.score=score;
        this.effectController = effectController;
    }

    @Override
    public IComp duplicate() {
        return new RacketCollision(sfunction, score, effectController);
    }

    @Override
    public void beginCollision(GameItem other) {
        sfunction.sendstring(json.toJson(new ReceiverInfo(80)));
        
        if (other.getName().equals("ball")) {
            if(!score.isStuck()){
                score.addCombo();
                combo=score.getCombo();
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
    public IComp duplicate() {
        return new BallCollision(wallSound, racketSound);
    }

    @Override
    public void beginCollision(GameItem other) {
        if ("racket".equals(other.getName())) {
            if (racketSound != null) {
                racketSound.play();
            }
        } else {
            // wishing something collided with me must be wall...
            if (wallSound != null) {
                wallSound.play();
            }
        }
    }

    @Override
    public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
    }
    
    
}

public class GameScene extends Scene {
    public static short GROUP_WALL = 1;
    public static short GROUP_BALL = 2;
    public static short GROUP_RACKET = 4;
    public static short GROUP_DETECTOR = 8;

	GameItem ballItem;
	GameItem racketItem ;
	Option option;

    private ReceiverFunction rfunction;
    private SenderFunction sfunction;
    
    private Sound wallSound;
    private Sound racketSound;
    private Music bgroundSound;
	public GameScene (ReceiverFunction rfunction, SenderFunction sfunction, Option o) {
		super();
		option=o;
		score= new Score(option);
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.sfunction.setpackage("com.odk.pairpongsender");
		if(option.gamemode==1)
			score.setLife(5);
		if(option.gamemode==2)
			score.setLife(1);
	}

    @Override
	public void reserveItem(Scene scene, ItemReservable coreProxy) {
    	float Frictions=0.05f;
    	float Restitutions=0.98f;
    	if (bgroundSound != null) {
    		bgroundSound.play();
        }
    	GameItem boardItembo = new GameItem(),
    			 debugdrawItem = new GameItem(new BtDebugDrawerComp()),
				 wallItem = new GameItem(),
                 lightItem = new GameItem(),
                 pointlightItem = new GameItem(new Transform(new Vector3(0, 1, 0)),
                		 						new PointLightComp(10).setColor(1, 1, 1, 1)),
                 removerItem = new GameItem();

        boardItembo.as(Transform.class).modify().setTranslation(1f, 0, 0);
        boardItembo.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(3.f, .1f, 3.f)), new CollisionFilter(GROUP_WALL, GROUP_BALL))
                      .setFriction(Frictions)
                      .setRestitution(Restitutions));
        GameItem boardItemt = boardItembo.duplicate(new Vector3(1f, 4.0f, 0));
        GameItem boardItemba = boardItembo.duplicate(new Vector3(4.1f, 2.0f, 0),
        		   new Quaternion(new Vector3(0,0,1), 90));
        boardItembo.add(new ModelComp(boxModelbo));
        boardItemba.add(new ModelComp(boxModelba));
        boardItemba.add(new BackCollision(score));
        boardItemt.add(new ModelComp(boxModelt));
       
        btCompoundShape racketCollShape = new btCompoundShape();
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(-.75f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.48f, .1f, .42f)));
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(.45f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.9f, .1f, .1f)));

        racketItem = new GameItem(new Transform(new Vector3(-2, 2.2f, 0),
			 new Quaternion(new Vector3(0, 0, -1), 90),
			 new Vector3(0.03f*(1.5f-0.5f*option.racketsize), 0.03f*(1.5f-0.5f*option.racketsize),
					 0.03f*(1.5f-0.5f*option.racketsize))));
        racketItem.add(BtRigidBodyComp
                      .dynamicBody(racketCollShape, 1000, new CollisionFilter(GROUP_RACKET, GROUP_BALL))
                      .setFriction(Frictions)
                      .activate()
                      .setRestitution(Restitutions)
                      .forceGravity(new Vector3()));
        racketItem.setName("racket");
        
        
        wallItem.as(Transform.class).modify().setTranslation(1f, 2.0f, 3.1f);
        wallItem.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(3.f, 2.f, .1f)), new CollisionFilter(GROUP_WALL, GROUP_BALL))
                      .setFriction(Frictions)
                      .setRestitution(Restitutions));
        GameItem wallItem2 = wallItem.duplicate(new Vector3(1f, 2.0f, -3.1f));
        wallItem.add(new ModelComp(boxModels));
        wallItem2.add(new ModelComp(boxModels2));
        ballItem = new GameItem(new Transform(0, 0.2f, 0));
        ballItem.add(new BallCollision(wallSound, racketSound));
        ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.15f), 1, 
                                                 new CollisionFilter(GROUP_BALL, 
                                                                     (short)(GROUP_DETECTOR | GROUP_WALL | GROUP_RACKET)))
                     .setAngularVelocity(new Vector3(0, 0, -10))
                     .setLinearVelocity(new Vector3(6, 2, 1))
                     .setRestitution(0.9f));
        ballItem.add(new ModelComp(ballModel));
        ballItem.setName("ball");

        // Tunneling-proof 
        btRigidBody ballBody = ballItem.as(BtRigidBodyComp.class).getRigidBody();
        ballBody.setCcdMotionThreshold(1e-4f);
        ballBody.setCcdSweptSphereRadius(2f);
       
        racketItem.add(new AssetModelComp("racket.obj"));
        racketItem.add(new RacketCollision(sfunction, score,
                new EffectController() {
                    @Override
                    public void invokeHit(Vector3 position) {
                        Vector3 screen = core().server(CameraServer.class).getCamera().project(position);
                        hitEffect.setPosition(new Vector2(screen.x, screen.y), new Vector2(screen.x, screen.y + 30));
                        hitEffect.rewind();
                    }
        }));
        racketItem.setName("racket");

        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.5f)).setColor(1.f, 1.f, 1.f, 1.0f));
        GameItem newBallItem = ballItem.duplicate(new Vector3(0, 2.8f, 0));
        newBallItem.setName("ball");
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
        coreProxy.reserveItem(debugdrawItem);
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0, 1, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(new GameItem(new Transform(new Vector3(0, 2, 0)),
                		 					new PointLightComp(50).setColor(1, 1, 1, 1)));
        coreProxy.reserveItem(removerItem);
        coreProxy.reserveItem(new GameItem(new SimpleCameraControllerComp()));
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
    private Texture bottom, top, side, side2, back, ballTex;
    private Texture hitTex;
    private TextureRegion hitTexReg;
    private HUDSpriteEffect hitEffect;
    
    Score score;
	BitmapFont bfont;
	SpriteBatch batch;
    private Json json = new Json();
    private String lastUUID = "";
    private StateInterpolater posXIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater posYIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    float rawTheta = 0;
    static final int RACKET_SPEED = 5;
    int time=0;
    @Override
    public void postRender() {
    	Random random = new Random();
        String infoString = rfunction.getstring();
        if (infoString != null && !infoString.equals("")) {
            SenderInfo newInfo = json.fromJson(SenderInfo.class, infoString);
            if (newInfo != null && !newInfo.uuid.equals(lastUUID)) {
                posXIntp.setDestState((newInfo.posX - 0.5f) * 6.3f);
                posYIntp.setDestState(newInfo.posY * 4.2f);
                rawTheta = newInfo.theta;
                lastUUID = newInfo.uuid;
            }
        }
        time++;
        if(time>1800){
        	time=0;
        	if (bgroundSound != null) {
        		bgroundSound.stop();
        		bgroundSound.play();
            }
        }
        if(score.needRegen()){
        	core.removeItem(core.getItemWithName("ball"));
        	GameItem newBall = ballItem.duplicate(new Vector3(0, 2.8f, 0));
    		newBall.as(BtRigidBodyComp.class).setLinearVelocity(
    				new Vector3(6, 2, random.nextFloat()*3-1.5f));
    		newBall.setName("ball");
    		core().addItem(newBall);
    		score.resetCombo();
        	score.setLife(score.getLife()-1);
        }
        if(score.reduceStuck()&&option.scoremode!=1){
        	score.addVScore(core.getItemWithName("ball").as(BtRigidBodyComp.class).getLinearVelocity().len());
        }
        if(score.getLife()==0){
        	sfunction.sendint(7);//占쏙옙占쏙옙트占쏙옙 占쏙옙占쌘억옙 占쏙옙환 占시그놂옙
        	SceneMgr.switchScene(new ScoreScene(rfunction, sfunction,score.getScore()));
        }
        if(option.gamemode==0)
        	score.timePass();
        if(!rfunction.getbool()){//占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占� 占쏙옙占쏙옙
        	SceneMgr.switchScene(new MainScene(rfunction, sfunction));
        }
    	batch.begin();
    	bfont.setColor(Color.WHITE);
        bfont.draw(batch, "Score : "+String.valueOf(score.getScore()), 0, 720);
        if(score.getScore()>option.highscores[0]){
        	bfont.draw(batch, "1st Score!!", 800, 720);
        }
        for(int i=0;i<4;i++){
        	if(score.getScore()<option.highscores[i]&&score.getScore()>option.highscores[i+1]){
        		bfont.draw(batch, String.valueOf(i+1)+"th Score : "+String.valueOf(option.highscores[i]),
        				800, 720);
        		bfont.draw(batch, "last : "+String.valueOf(option.highscores[i]-score.getScore()), 800, 640);
        	}
        }
        if(score.getScore()<option.highscores[4]){
        	bfont.draw(batch, "5th Score : "+String.valueOf(option.highscores[4]),
    				800, 720);
    		bfont.draw(batch, "last : "+String.valueOf(option.highscores[4]-score.getScore()), 800, 640);
        }
        if((score.getLife()<300&&option.gamemode==0)||score.getLife()<2)
        	bfont.setColor(Color.RED);
        if(option.gamemode==0)
        	bfont.draw(batch, String.valueOf(score.getLife()/30), 600, 720);
        else
        	bfont.draw(batch, "last ball : "+String.valueOf(score.getLife()-1), 450, 720);
        if(score.showScore()){
        	bfont.setColor(Color.RED);
        	bfont.draw(batch, String.valueOf(score.getCombo())+" Combo : "+
        	String.valueOf((int)((2*score.getCombo()-1)*100*(0.5+0.5*option.racketsize)*(1+0.1*option.gamemode))), 0, 640);
        	if(!score.isStuck()&&option.scoremode!=1){
        		bfont.setColor(Color.GREEN);
        		bfont.draw(batch, " Velocity bonus : "+String.valueOf(score.getVscore()), 0, 560);
        	}
        }
        if(score.showAScore()){
        	bfont.setColor(Color.BLUE);
        	bfont.draw(batch, " Accuacy bonus : "+String.valueOf(score.getAscore()), 0, 480);
        }

        // HUD sprites
        if (hitEffect.isRunning()) {
            hitEffect.render(batch);
            hitEffect.advance(Gdx.graphics.getDeltaTime());
        } else {
        }
        batch.end();
        BtRigidBodyComp bodyComp = racketItem.as(BtRigidBodyComp.class);
        Vector3 racketTr = racketItem.getTransform().getTranslation(new Vector3());

        Quaternion racketOri = new Quaternion();
        bodyComp.getRigidBody().getWorldTransform().getRotation(racketOri, true);

        posXIntp.setState(racketTr.z);
        posYIntp.setState(racketTr.y);
        
        float destTheta;
        /*
        if (rawTheta < 45) {
            destTheta = 60;
        } else
            destTheta = 120;
            */
        destTheta = (90 + (70 - rawTheta) * 0.8f) ;
        
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
            scale = RACKET_SPEED;
        } else
            scale = -RACKET_SPEED;

        bodyComp.activate();
        bodyComp.setLinearVelocity(new Vector3(0, posYIntp.getVelocity(), posXIntp.getVelocity()));
        bodyComp.setAngularVelocity(axis.scl(scale));
        
        
    }

    @Override
    public void firstPreparation() {
    	bfont= new BitmapFont();
    	batch = new SpriteBatch();
        bfont.setColor(Color.WHITE); bfont.scale(3f);
        bottom = new Texture("bottom.png");
        back = new Texture("back.png");
        top = new Texture("top.png");
        side = new Texture("side.png");
        side2 = new Texture("side2.png");
        ballTex = new Texture("ball.jpg");
        hitTex  = new Texture("Effect_hit.png");
        hitTexReg = new TextureRegion(hitTex);
        
        hitEffect = new HUDSpriteEffect(hitTexReg, 0.5f, new Vector2())
                    .setScale(0.2f, 0.2f)
                    .setAlpha(1.f, 0f);


        wallSound = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
        racketSound = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
        bgroundSound = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        boxModelbo = new ModelBuilder().createBox(6, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(bottom))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelba = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(back))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModelt = new ModelBuilder().createBox(6, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(top))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels = new ModelBuilder().createBox(6, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(side))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModels2 = new ModelBuilder().createBox(6, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(side2))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        ballModel = new ModelBuilder().createSphere(.3f, .3f, .3f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.25f, .25f, .25f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(ballTex))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates); 
    }

    @Override
    public void tearDown() {
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
        if (bgroundSound != null)
        	bgroundSound.dispose();
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
