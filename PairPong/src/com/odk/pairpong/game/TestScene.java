package com.odk.pairpong.game;

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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
import com.badlogic.gdx.utils.Json;

class MyCollision extends CollisionComp {
    
	@Override
	public IComp duplicate() {
		return new MyCollision();
	}

	@Override
	public void beginCollision(GameItem other) {
		core().removeItem(other);
	}

	@Override
	public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
	}
}

class VibCollision extends CollisionComp {
    private Json json = new Json();
    private SenderFunction sfunction;
    public VibCollision (SenderFunction sfunction) {
        this.sfunction = sfunction;
    }

    @Override
    public IComp duplicate() {
        return new VibCollision(sfunction);
    }

    @Override
    public void beginCollision(GameItem other) {
        sfunction.sendstring(json.toJson(new ReceiverInfo(80)));
    }

    @Override
    public void endCollision(GameItem other, Iterable<CollisionInfo> info) {
    }
    
}

public class TestScene extends Scene {
	GameItem ballItem;
	GameItem racketItem ;


    private ReceiverFunction rfunction;
    private SenderFunction sfunction;
	public TestScene (ReceiverFunction rfunction, SenderFunction sfunction) {
		super();
		this.rfunction = rfunction;
		this.sfunction = sfunction;
		this.sfunction.setpackage("com.odk.pairpongsender");
	}

    @Override
	public void reserveItem(Scene scene, ItemReservable coreProxy) {     
    	GameItem boardItem = new GameItem(),
    			 debugdrawItem = new GameItem(new BtDebugDrawerComp()),
				 wallItem = new GameItem(),
                 lightItem = new GameItem(),
                 pointlightItem = new GameItem(new Transform(new Vector3(0, 1, 0)),
                		 						new PointLightComp(20).setColor(1, 1, 1, 1)),
                 removerItem = new GameItem();

        boardItem.as(Transform.class).modify().setTranslation(0, 0, 0);
        boardItem.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(2.f, .1f, 3.f)))
                      .setFriction(0.1f)
                      .setRestitution(0.98f));
        boardItem.add(new ModelComp(boxModel));
       
        btCompoundShape racketCollShape = new btCompoundShape();
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(-.75f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.48f, .03f, .42f)));
        racketCollShape.addChildShape(new Matrix4().set(new Vector3(.45f, .03f, .03f), new Quaternion()),
        		new btBoxShape(new Vector3(.9f, .03f, .03f)));

        racketItem = new GameItem(new Transform(new Vector3(0, 2, 0),
			 new Quaternion(),
			 new Vector3(0.03f, 0.03f, 0.03f)));
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
        wallItem.add(new ModelComp(boxModel2));

        ballItem = new GameItem(new Transform(0, 0.2f, 0));
        ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.15f), 1)
                     .setAngularVelocity(new Vector3(0, 0, -10))
                     .setLinearVelocity(new Vector3(2, 1, 2))
                     .setRestitution(0.98f));
        ballItem.add(new ModelComp(ballModel));
        ballItem.setName("ball");
       
       
        racketItem.add(new AssetModelComp("racket.obj"));
       

        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.5f)).setColor(1.f, 1.f, 1.f, 1.0f));

        coreProxy.reserveItem(ballItem.duplicate(new Vector3(0, 2.8f, 0)));
       
        removerItem.as(Transform.class).modify().setTranslation(0, -10f, 0);

        removerItem.add(new BtDetectorComp(new btBoxShape(new Vector3(50.f, 1f, 50.f))));
        removerItem.add(new MyCollision());
        removerItem.add(new ModelComp(ballModel));
        coreProxy.reserveItem(boardItem);
        coreProxy.reserveItem(boardItem.duplicate(new Vector3(0, 4.0f, 0)));
        coreProxy.reserveItem(boardItem.duplicate(new Vector3(2.1f, 2.0f, 0),
       		   new Quaternion(new Vector3(0,0,1), 90)));
        coreProxy.reserveItem(wallItem);
        coreProxy.reserveItem(wallItem.duplicate(new Vector3(0, 2.0f, -3.1f)));
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
                SceneMgr.switchScene(new TestScene(rfunction, sfunction));
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

	private Model boxModel, boxModel2, ballModel;
    private Texture tex;
    
    private Json json = new Json();
    private String lastUUID = "";
    private StateInterpolater posXIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater posYIntp = new StateInterpolater(0.1f, 1.f, 0, 10);
    private StateInterpolater thetaIntp = new StateInterpolater(1, 90, 0, 900);
    
    @Override
    public void postRender() {
        String infoString = rfunction.getstring();
        if (infoString != null && !infoString.equals("")) {
            SenderInfo newInfo = json.fromJson(SenderInfo.class, infoString);
            if (newInfo != null && !newInfo.uuid.equals(lastUUID)) {
                posXIntp.setDestState((newInfo.posX - 0.5f) * 3);
                posYIntp.setDestState(newInfo.posY * 4 + 0.2f);
                thetaIntp.setDestState(newInfo.theta * 1.5f);
                lastUUID = newInfo.uuid;
            }
        }
        posXIntp.update(0.03f);
        posYIntp.update(0.03f);
        thetaIntp.update(0.03f);

        racketItem.getTransform().modify().set(new Vector3(-2, 
                                                           posYIntp.getState(),
                                                           posXIntp.getState()),
                                               new Quaternion(new Vector3(0,0,1), thetaIntp.getState()),
                                               new Vector3(0.03f,0.03f, 0.03f));
    }

    @Override
    public void firstPreparation() {
        tex = new Texture("doge.jpg");
        boxModel = new ModelBuilder().createBox(4, .2f, 6, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(tex))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        boxModel2 = new ModelBuilder().createBox(4, 4, .2f, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createDiffuse(new TextureRegion(tex))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        ballModel = new ModelBuilder().createSphere(.3f, .3f, .3f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.95f, .95f, .95f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f)),
                Usage.Position | Usage.Normal); 
    }

    @Override
    public void tearDown() {
        boxModel.dispose();
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