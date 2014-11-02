package com.algy.schedcore.frontend.idl;

import java.util.Map;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.DirectionalLightComp;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.ModelComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class IDLTestScene extends Scene {
    private Model boxModel, ballModel;
    @Override
    public void postRender() {
    }

    private static Vector3 tr ( Map<String, IDLValue> dict) {
        float x = 0, y = 0, z = 0;
        if (dict.containsKey("x")) {
            x = dict.get("x").asFloat();
        }
        if (dict.containsKey("y")) {
            y = dict.get("y").asFloat();
        }
        if (dict.containsKey("z")) {
            z = dict.get("z").asFloat();
        }
        return new Vector3(x, y, z);
    }
    @Override
    public void firstPreparation() {
        IDLLoader.registerCompLoader(new IDLCompLoader("transform",
                new IDLCompCreator() {
                    @Override
                    public BaseComp create(Map<String, IDLValue> dict) {
                        return new Transform(tr(dict));
                    }
                }, 
                new IDLCompModifier() {
                    @Override
                    public void modify(BaseComp freshComp, Map<String, IDLValue> dict) {
                        ((Transform)freshComp).modify().setTranslation(tr(dict));
                    }
                    
                    @Override
                    public Class<? extends BaseComp> getType() {
                        return Transform.class;
                    }
                }));
        core.server(EnvServer.class).ambientLightColor.set(.2f, .2f, .2f, 1); 
        core.server(BtPhysicsWorld.class).world.setGravity(new Vector3(0, -9.8f, 0));
        
        boxModel = new ModelBuilder().createBox(4, .2f, 4, 
                new Material(ColorAttribute.createDiffuse(1f, 0.5f, 0.3f, 1f),
                             ColorAttribute.createSpecular(.3f, .3f, .3f, 1f)), 
                Usage.Position | Usage.Normal);
        ballModel = new ModelBuilder().createSphere(.8f, .8f, .8f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.95f, .95f, .95f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f)),
                Usage.Position | Usage.Normal); 
        
    }

    @Override
    public void prepare() {
        core.server(CameraServer.class).setPosition(new Vector3(3, .2f, 3))
                                       .lookAt(new Vector3(0, 0, 0)); 
        GameItem boardItem = new GameItem(), 
                 ballItem = new GameItem(),
                 lightItem = new GameItem();
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
        
        eden().putPrototype("ball", ballItem.duplicate());
        for (GameItem gameItem : IDLLoader.loadScene(Gdx.files.internal("Ang"), eden(), core.getServerItem()).first) {
            System.out.println("MADE!");
            core.addItem(gameItem);
        }
        

        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.2f)).setColor(1.f, 1.f, 1.f, .2f));
        for (float idx = 30; idx < 100; idx+=10) 
            core.addItem(ballItem.duplicate(new Vector3(0, idx * 0.1f, 0)));
        
        core.addItem(boardItem);
        core.addItem(lightItem);

        Done ();
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