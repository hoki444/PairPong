package com.odk.pairpong;

import com.algy.schedcore.IComp;
import com.algy.schedcore.frontend.ItemReservable;
import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.DirectionalLightComp;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.ModelComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtDetectorComp;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.algy.schedcore.middleend.bullet.CollisionComp;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

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

public class TestScene extends Scene {
	GameItem ballItem = new GameItem();

    @Override
	public void reserveItem(Scene scene, ItemReservable coreProxy) {     
		GameItem boardItem = new GameItem(), 
                 lightItem = new GameItem(),
                 removerItem = new GameItem();
		
       boardItem.as(Transform.class).modify().setTranslation(0, 0, 0);
       boardItem.add(BtRigidBodyComp
                     .staticBody(new btBoxShape(new Vector3(2.f, .1f, 2.f)))
                     .setFriction(0.7f)
                     .setRestitution(0.8f));
       boardItem.add(new ModelComp(boxModel));


       ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.4f), 1)
                    .setAngularVelocity(new Vector3(0, 0, -10))
                    .setLinearVelocity(new Vector3(0, 0, 0))
                    .setRestitution(0.95f));
       ballItem.add(new ModelComp(ballModel));
       

       lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
       lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.2f)).setColor(1.f, 1.f, 1.f, .2f));

       for (float idx = 30; idx < 100; idx+=10) 
           coreProxy.reserveItem(ballItem.duplicate(new Vector3(0, idx * 0.1f, 0)));
       
       removerItem.as(Transform.class).modify().setTranslation(0, -10f, 0);

       removerItem.add(new BtDetectorComp(new btBoxShape(new Vector3(500.f, 1f, 500.f))));
       removerItem.add(new MyCollision());
       removerItem.add(new ModelComp(ballModel));
       coreProxy.reserveItem(boardItem);
       coreProxy.reserveItem(lightItem);
       coreProxy.reserveItem(removerItem);
       Done ();

	}

	@Override
	public void endResourceInitialization(Scene scene) {
        core.server(EnvServer.class).ambientLightColor.set(.2f, .2f, .2f, 1); 
        core.server(BtPhysicsWorld.class).world.setGravity(new Vector3(0, -9.8f, 0));

        core.server(CameraServer.class).setPosition(new Vector3(1, 5f, 1))
                                       .lookAt(new Vector3(0, 0, 0))
                                       .setUpVector(new Vector3(1, 0, 0)); 
        Done ();
	}

	private Model boxModel, ballModel;
    private Texture tex;
    private SpriteBatch sb;
    private ReceiverFunction rfunction = new ReceiverFunction();
    private int n = 0;
    @Override
    public void postRender() {
        sb.begin();
        sb.draw(tex, 0, 0, 100, 100);
        n=n+rfunction.getint();
        if(n>100){
        	n=0;
        	core.addItem(ballItem.duplicate(new Vector3(1/10.0f, 3f, 0)));
        }
        sb.end();
    }

    @Override
    public void firstPreparation() {
    	sb  = new SpriteBatch();
        tex = new Texture("doge.jpg");
        boxModel = new ModelBuilder().createBox(4, .2f, 4, 
                new Material(ColorAttribute.createDiffuse(0.1f, 0.1f, 0.1f, 0.1f),
                             ColorAttribute.createSpecular(.7f, .7f, .7f, 1f),
                             TextureAttribute.createSpecular(new TextureRegion(tex))),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        ballModel = new ModelBuilder().createSphere(.8f, .8f, .8f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.95f, .95f, .95f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f)),
                Usage.Position | Usage.Normal); 
    }

    @Override
    public void tearDown() {
        boxModel.dispose();
        sb.dispose();
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