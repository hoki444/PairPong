package com.algy.schedcore.frontend;

import com.algy.schedcore.ISchedTask;
import com.algy.schedcore.ITickGetter;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.GameCore;
import com.algy.schedcore.middleend.InputPushServer;
import com.algy.schedcore.middleend.OrthoCamServer;
import com.algy.schedcore.middleend.PerspCamServer;
import com.algy.schedcore.middleend.RenderServer;
import com.algy.schedcore.middleend.asset.AssetManagerServer;
import com.algy.schedcore.middleend.asset.Eden;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class Scene {
    private SceneState state = SceneState.Preparing;
    private SceneState nextState = SceneState.Preparing;
    private ModelBatch modelBatch;
    private ITickGetter tickGetter = ITickGetter.systemTickGetter;

    protected GameCore core = new GameCore(ITickGetter.systemTickGetter);
    protected SceneMgr manager;
    protected SceneConfig config;
    
    boolean first = true;

    public void setManager (SceneMgr mgr) {
        this.manager = mgr;
    }
    
    public AssetManager assetManager () {
        return manager.assetManager;
    }

    public Eden eden () {
        return manager.eden;
    }

    static enum SceneState {
        Preparing, Running, TearingDown, ChangingScene
    };
    
    SceneState getSceneState () {
        return state;
    }
    
    public Scene () {
        this.config = SceneConfig.defaultConfig();
    }
    
    public Scene (SceneConfig config) {
        this.config = config;
    }

    private BtPhysicsWorld worldServer;
    void internalPreparation () {
        this.modelBatch = new ModelBatch();

        RenderServer renderServer = new RenderServer();
        CameraServer cameraServer;
        EnvServer envServer;
        InputPushServer inputServer;
        AssetManagerServer assetManagerServer;
        if (config.perspectiveCamera)
            cameraServer = new PerspCamServer(Gdx.graphics.getWidth(), 
                                              Gdx.graphics.getHeight(), 
                                              config.fieldOfView);
        else
            cameraServer = new OrthoCamServer(Gdx.graphics.getWidth(), 
                                              Gdx.graphics.getHeight());
        cameraServer.setPosition(new Vector3(0, 0, 1));
        cameraServer.lookAt(new Vector3(0, 0, 0));

        if (config.useBulletPhysics) {
            if (!SceneMgr.bulletInitialized) {
                SceneMgr.initBullet();
            }
            worldServer = new BtPhysicsWorld(new Vector3(0, -9.8f, 0), 20);
        } else
            worldServer = null;

        envServer = new EnvServer();
        inputServer = new InputPushServer();
        assetManagerServer = new AssetManagerServer(assetManager());
        
        core.addServer(renderServer);
        core.addServer(cameraServer);
        core.addServer(envServer);
        core.addServer(inputServer);
        core.addServer(assetManagerServer);
        core.addServer(eden());
        core.addServer(worldServer);

        
        Gdx.graphics.setContinuousRendering(true);

    }
    
    private class RenderWork implements ISchedTask {
        @Override
        public void schedule(SchedTime time) {
            Environment env;
            modelBatch.begin(core.server(CameraServer.class).getCamera());
            env = core.server(EnvServer.class).makeEnvironment();
            core.server(RenderServer.class).render(modelBatch, env);
            modelBatch.end();
        }

        @Override
        public void beginSchedule() {
        }

        @Override
        public void endSchedule() {
        }
    }
    
    private String RENDER_SIG = "DrawIt!";
    
    void internalPreRender () {
        if (first) {
            long period = config.itemRenderingPeriod;
            core.sched().addPeriodic(tickGetter.getTickCount(), 
                                     new RenderWork(), 
                                     period, 
                                     0, 
                                     RENDER_SIG);
            first = false;
        }
    }
    
    
    void syncSceneState () {
        if (state != nextState) {
            this.state = nextState;
            first = true;
        }
    }
    
    protected final void Done () {
        switch (state) {
        case Preparing:
            nextState = SceneState.Running;
            break;
        case Running:
            nextState = SceneState.TearingDown;
            break;
        case TearingDown:
            nextState = SceneState.ChangingScene;
            break;
        case ChangingScene:
            break;
        default:
            break;
        }
    }
    

    public final void render () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(config.backgroundColor.r,
                            config.backgroundColor.g,
                            config.backgroundColor.b, 
                            config.backgroundColor.a);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        
        while (core.sched().runOnce(tickGetter) != RENDER_SIG);
    }
    
    public final void destroy () {
        modelBatch.dispose();
        core.clearAll();
    }
    
    public abstract void postRender ();
    public abstract void firstPreparation ();
    public abstract void prepare ();
    public abstract void tearDown ();
    public abstract void resize (int width, int height);
    public abstract void pause ();
    public abstract void resume ();
}