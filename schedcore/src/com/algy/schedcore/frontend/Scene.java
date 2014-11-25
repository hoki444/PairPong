package com.algy.schedcore.frontend;

import java.util.ArrayList;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.ISchedTask;
import com.algy.schedcore.ITickGetter;
import com.algy.schedcore.Item;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.GameCore;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.InputPushServer;
import com.algy.schedcore.middleend.Render3DServer;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetLoadingController;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.LazyAssetManager;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

class UpdatableItemRsrvTbl implements ItemReservable {
    private Item<BaseCompServer, Object> serverItem = new Item<BaseCompServer, Object>(BaseCompServer.class);
    private ArrayList<GameItem> gameItemList = new ArrayList<GameItem>();

    @Override
    public void reserveServer(BaseCompServer server) {
        if (serverItem.has(server.getClass())) {
            serverItem.remove(server.getClass());
        }
        serverItem.add(server);
    }
    
    @Override
    public void reserveItem(GameItem gameItem) {
        gameItemList.add(gameItem);
    }

    @Override
    public Iterable<GameItem> reservedItems() {
        return gameItemList;
    }

    @Override
    public Iterable<BaseCompServer> reservedServers() {
        return serverItem;
    }

    @Override
    public void reserveServer(Iterable<BaseCompServer> servers) {
        for (BaseCompServer server : servers)
            reserveServer(server);
    }

    @Override
    public void reserveItem(Iterable<GameItem> gameItems) {
        for (GameItem gameItem : gameItems)
            reserveItem(gameItem);
    }
}

public abstract class Scene implements SceneResourceInitializer, IDLGameContext {
    /*
     *  Scene Stages
     *  --------------
     *  Preparation
     *    - When user wants to initialize sth, it takes place here.
     *    - Prototype loading takes place here in common 
     *    
     *  InitResource
     *  [
     *      SceneMaking 
     *        - Define which items or servers are added to core in future. 
     *          In fact, items added here are not added to core immediately but inserted in 
     *          core's reservation queue.
     *      
     *      AssetGathering
     *        - The assets which scene items defined the previous stage need are already gathered
     *    
     *      AssetLoading 
     *        - load items a
     *        - As a last work of this stage, reserved items and servers, inserted in SceneMaking stage, 
     *          are added to core indeed, and schedulable components are scheduled. 
     *  ]
     *    
     *  Running
     *    - postRun () method is called after all rendering jobs in core done.
     *      
     *  TearingDown
     *    - At last, destroy all items and servers
     *    
     *  ChangingScene
     *    - A stage in which scene is ready to be replaced by other scene.
     */

    static enum SceneState {
        Preparing, InitResource, Running, TearingDown, ChangingScene
    };
    
    static enum InitState {
        Begin, SceneMaking, AssetGathering, AssetLoading, End
    };

    /*
     * Game Screen states
     */
    private SceneState state = SceneState.Preparing;
    private SceneState nextState = SceneState.Preparing;

    /*
     * States for resource initialization
     */
    private SceneResourceInitializer sceneResourceInitializer;
    private InitState initState;
    private ItemReservable itemReservable;
    private AssetGatherer assetGatherer;
    private AssetLoadingController loadingController;

    private ModelBatch modelBatch;

    private ITickGetter tickGetter = ITickGetter.systemTickGetter;
    protected final GameCore core = new GameCore(ITickGetter.systemTickGetter);

    protected SceneMgr manager;
    protected SceneConfig config;
    boolean first = true;
    
    {
        initRIState(this);
    }
    
    public final void setManager (SceneMgr mgr) {
        this.manager = mgr;
    }
    
    public final LazyAssetManager assetManager () {
        return manager.assetManager;
    }

    public final Eden eden () {
        return manager.eden;
    }
    
    public final GameCore core () {
        return core;
    }

    SceneState getSceneState () {
        return state;
    }
    
    public Scene () {
        this.config = SceneConfig.defaultConfig();
    }
    
    public Scene (SceneConfig config) {
        this.config = config;
    }

    protected void reserveDefaultServers (ItemReservable reservable) {
        Render3DServer renderServer = new Render3DServer();
        CameraServer cameraServer;
        EnvServer envServer;
        InputPushServer inputServer;
        AssetServer assetServer;
        if (config.perspectiveCamera)
            cameraServer = new CameraServer(Gdx.graphics.getWidth(), 
                                            Gdx.graphics.getHeight(), 
                                            config.fieldOfView);
        else
            cameraServer = new CameraServer(Gdx.graphics.getWidth(), 
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
        assetServer = new AssetServer(assetManager());
        
        reservable.reserveServer(renderServer);
        reservable.reserveServer(envServer);
        reservable.reserveServer(cameraServer);
        reservable.reserveServer(inputServer);
        reservable.reserveServer(eden());
        reservable.reserveServer(assetServer);
        if (worldServer != null) {
            reservable.reserveServer(worldServer);
        }
    }

    private BtPhysicsWorld worldServer;
    
    private int renderTaskId = -1;
    void internalPreparation () {
        this.modelBatch = new ModelBatch();
        Gdx.graphics.setContinuousRendering(true);
        
        long period = config.itemRenderingPeriod;
        renderTaskId = core.sched().addPeriodic(tickGetter.getTickCount(), 
                                 new RenderWork(), 
                                 period, 
                                 0, 
                                 RENDER_SIG);
        suspendRendering();
    }

    protected void suspendRendering () {
        core().sched().suspend(renderTaskId, tickGetter);
    }

    protected void resumeRendering () {
        core().sched().resume(renderTaskId, tickGetter);
    }
    
    private class RenderWork implements ISchedTask {
        @Override
        public void schedule(SchedTime time) {
            Environment env;
            if (core.getServerItem().has(CameraServer.class)) {
                modelBatch.begin(core.server(CameraServer.class).getCamera());
                env = core.server(EnvServer.class).makeEnvironment();
                core.server(Render3DServer.class).render(modelBatch, env);
                modelBatch.end();
            }
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
            resumeRendering();
            first = false;
        }
    }
    
    void syncSceneState () {
        if (state != nextState) {
            this.state = nextState;
            first = true;
        }
    }
    
    public final void Done () {
        switch (state) {
        case Preparing:
            nextState = SceneState.InitResource;
            break;
        case InitResource:
            if (setNextRIState())
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
    
    public void clearBuffers () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(config.backgroundColor.r,
                            config.backgroundColor.g,
                            config.backgroundColor.b, 
                            config.backgroundColor.a);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    

    final void render () {
        while (core.sched().runOnce(tickGetter) != RENDER_SIG);
    }
    
    final void destroy () {
        modelBatch.dispose();
        core.clearAll();
    }
    
    final void doInitResource () {
        switch (initState) {
        case Begin:
            sceneResourceInitializer.beginResourceInitialization(this);
            break;
        case SceneMaking:
            sceneResourceInitializer.reserveItem(this, itemReservable);
            break;
        case AssetGathering:
            sceneResourceInitializer.gatherAsset(this, assetGatherer);
            break;
        case AssetLoading:
            sceneResourceInitializer.loadAsset(this, 
                                               loadingController, 
                                               assetGatherer.getGatheredAssetList());
            break;
        case End:
            sceneResourceInitializer.endResourceInitialization(this);
            break;
        default:
            break;
        }
    }
    
    protected final void initializeResource (SceneResourceInitializer initializer) {
        nextState = SceneState.InitResource;
        initRIState(initializer);
    }

    private final void initRIState (SceneResourceInitializer initializer) {
        sceneResourceInitializer = initializer;
        initState = InitState.Begin;
    }
    
    private boolean setNextRIState () {
        switch (initState) {
        case Begin:
            initState = InitState.SceneMaking;
            itemReservable = new UpdatableItemRsrvTbl();
            reserveDefaultServers(itemReservable);
            break;
        case SceneMaking:
            initState = InitState.AssetGathering;
            assetGatherer = new AssetGatherer(eden());
            assetGatherer.gatherFromGameItem(itemReservable.reservedItems());
            break;
        case AssetGathering:
            initState = InitState.AssetLoading;
            loadingController = assetManager().load(assetGatherer.getGatheredAssetList());
            break;
        case AssetLoading:
            initState = InitState.End;
            for (BaseCompServer server : itemReservable.reservedServers()) {
                core.addServer(server);
            }
            core.addGameItemAll(itemReservable.reservedItems());
            itemReservable = null;
            assetGatherer = null;
            loadingController = null;
            break;
        case End:
            return true;
        default:
            break;
        }
        return false;
    }
    

    public abstract void firstPreparation ();
    public abstract void postRender ();
    public abstract void resize (int width, int height);
    public abstract void pause ();
    public abstract void resume ();

    protected void prepare () { Done (); }
    protected void tearDown () { Done (); }
    @Override
    public void reserveItem (Scene scene, ItemReservable coreProxy) {
        Done ();
    }

    @Override
    public void gatherAsset (Scene scene, AssetGathererProxy gathererProxy) {
        Done ();
    }

    @Override
    public void loadAsset (Scene scene, AssetLoadingController controller, AssetList newAssetList) {
        controller.join();
        Done ();
    }

    @Override
    public void beginResourceInitialization (Scene scene) {
        Done ();
    }

    @Override
    public void endResourceInitialization (Scene scene) {
        Done ();
    }
}