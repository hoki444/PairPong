package com.algy.schedcore.middleend;

import com.algy.schedcore.Core;
import com.algy.schedcore.IComp;
import com.algy.schedcore.SchedTask;
import com.algy.schedcore.TickGetter;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.Scheduler;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.algy.schedcore.middleend.bullet.CollisionComp;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class MyGame extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private Core core;
    
    // Component Servers
    private Render3DServer renderServer;
    private CameraServer cameraServer;
    private BtPhysicsWorld worldServer;
    private EnvServer envServer;
    private InputPushServer inputServer;
    
    // Game Items
    private GameItem boardItem;
    private GameItem ballItem;
    private GameItem lightItem;
    
    
    // Models
    Model boxModel;
    Model ballModel;

    @Override
    public void create () {
        Bullet.init();
        modelBatch = new ModelBatch();

        core = new Core(Scheduler.MilliScheduler());
        
        renderServer = new Render3DServer();
        cameraServer = new CameraServer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 67.f);
        cameraServer.setPosition(new Vector3(0, 3, 4));
        cameraServer.lookAt(new Vector3(0, 0, 0));
        worldServer = new BtPhysicsWorld(new Vector3(0, -9.8f, 0), 20);
        envServer = new EnvServer();
        envServer.ambientLightColor.set(.2f, .2f, .2f, 1); 
        inputServer = new InputPushServer();
        
        core.addServer(renderServer);
        core.addServer(cameraServer);
        core.addServer(worldServer);
        core.addServer(envServer);
        core.addServer(inputServer);
        
        boxModel = new ModelBuilder().createBox(4, .2f, 4, 
                new Material(ColorAttribute.createDiffuse(1f, 0.5f, 0.3f, 1f),
                             ColorAttribute.createSpecular(.3f, .3f, .3f, 1f)), 
                Usage.Position | Usage.Normal);
        
        boardItem = new GameItem();
        boardItem.as(Transform.class).modify().setTranslation(0, 0, 0);
        boardItem.add(BtRigidBodyComp
                      .staticBody(new btBoxShape(new Vector3(2.f, .1f, 2.f)))
                      .setFriction(0.7f)
                      .setRestitution(0.8f));
        boardItem.add(new ModelComp(boxModel));

        
        ballModel = new ModelBuilder().createSphere(.8f, .8f, .8f, 10, 10, 
                new Material(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1f),
                             ColorAttribute.createSpecular(.95f, .95f, .95f, 1f),
                             ColorAttribute.createAmbient(.1f, .2f, .1f, 1f)),
                Usage.Position | Usage.Normal); 
        
        ballItem = new GameItem();
        ballItem.as(Transform.class).modify().setTranslation(0, 0.8f, 0);
        ballItem.add(BtRigidBodyComp.dynamicBody(new btSphereShape(.4f), 1)
                     .setAngularVelocity(new Vector3(-1, 0, 0))
                     .setLinearVelocity(new Vector3(0, 0, 0))
                     .setRestitution(1f));
        ballItem.add(new ModelComp(ballModel));
        ballItem.add(new CollisionComp() {
            @Override
            public IComp duplicate() {
                return null;
            }
            
            @Override
            protected void onDetached() {
            }
            
            @Override
            protected void onAdhered() {
            }
            
            @Override
            public void beginCollision(GameItem other) {
                System.out.println("BEGIN ");
            }

            @Override
            public void endCollision(GameItem other,
                    Iterable<CollisionInfo> info) {
                int count = 0;
                System.out.println(info.iterator().hasNext());
                for (CollisionInfo _ : info)
                    count++;
                System.out.println("END (" + count + ") " + info.iterator().next().getImpulse());
            }
        });
        ballItem.add(new InputComp() {

            @Override
            public IComp duplicate() {
                return null;
            }
            
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return true;
            }
            
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                CameraServer camserver = server(CameraServer.class);
                Vector3 vec = new Vector3(camserver.getPosition());
                vec.add(.1f, 0, 0);
                camserver.setPosition(vec);
                return false;
            }
            
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }
            
            @Override
            public boolean scrolled(int amount) {
                return false;
            }
            
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }
            
            @Override
            public boolean keyUp(int keycode) {
                return true;
            }
            
            @Override
            public boolean keyTyped(char character) {
                return false;
            }
            
            @Override
            public boolean keyDown(int keycode) {
                return true;
            }
            
            @Override
            protected void onDetached() {
            }
            
            @Override
            protected void onAdhered() {
            }
        });
        

        lightItem = new GameItem();
        lightItem.as(Transform.class).get().setTranslation(0, 2, 0);
        lightItem.add(new DirectionalLightComp(new Vector3(0, -1f, 0.2f)).setColor(1.f, 1.f, 1.f, .2f));
        
        core.addItem(boardItem);
        core.addItem(ballItem);
        core.addItem(lightItem);
        for (float idx = 30; idx < 150; idx+=10) 
            core.addItem(ballItem.duplicate(new Vector3(0, 
                                                        0, 
                                                        0)));

        core.scheduler().addPeriodic( new RenderWork(), 20, 0, DRAW_SIG);

        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.setVSync(true);
    }

    @Override
    public void render () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.3f);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        while (true) {
            Object ans;
            // long start = System.nanoTime();
            ans = core.scheduler().runOnce();
            // long end = System.nanoTime();
            
            if (ans == DRAW_SIG) break;
            // System.out.println((end - start) / 1000 + " us");
        }
        // Gdx.graphics.requestRendering();
    }
    
    private static String DRAW_SIG = "Drawit";

    class RenderWork implements SchedTask {
        Texture tex;
        public RenderWork () {
        }
        public void onScheduled(SchedTime time) {
        }

        public void beginSchedule() {
        }

        public void endSchedule() {
        }
    }
    
    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}
