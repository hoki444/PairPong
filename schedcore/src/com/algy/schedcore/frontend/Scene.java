package com.algy.schedcore.frontend;

import java.util.ArrayList;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.CompMgrSpace;
import com.algy.schedcore.GameItem;
import com.algy.schedcore.GameItemSpace;
import com.algy.schedcore.SchedTask;
import com.algy.schedcore.SchedTaskConfig;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.Scheduler;
import com.algy.schedcore.TaskController;
import com.algy.schedcore.event.GameEventMgr;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.middleend.InputPushServer;
import com.algy.schedcore.middleend.Render3DServer;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetLoadingController;
import com.algy.schedcore.middleend.asset.AssetServer;
import com.algy.schedcore.middleend.asset.LazyAssetManager;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.util.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

class UpdatableItemRsrvTbl implements ItemReservable {
    private CompMgrSpace serverItem = new CompMgrSpace(null);
    private ArrayList<GameItem> gameItemList = new ArrayList<GameItem>();

    @Override
    public void reserveServer(BaseCompMgr server) {
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
    public Iterable<BaseCompMgr> reservedServers() {
        return serverItem;
    }

    @Override
    public void reserveServer(Iterable<BaseCompMgr> servers) {
        for (BaseCompMgr server : servers)
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
    SceneState nextState = SceneState.Preparing;

    /*
     * States for resource initialization
     */
    private SceneResourceInitializer sceneResourceInitializer;
    private InitState initState;
    private ItemReservable itemReservable;
    private AssetGatherer assetGatherer;
    private AssetLoadingController loadingController;

    private ModelBatch modelBatch;

    private final Scheduler scheduler = Scheduler.MilliScheduler();
    private final GameEventMgr gameEventMgr = new GameEventMgr(scheduler);

    private SchedulerUpdater schedUpdater = new SchedulerUpdater(scheduler);

    protected final GameItemSpace core = new GameItemSpace(scheduler);

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
    
    public final GameItemSpace core () {
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
            synchronized (SceneMgr.class) {
                if (!SceneMgr.bulletInitialized) {
                    BtPhysicsWorld.initBullet();
                }
            }
            worldServer = new BtPhysicsWorld(new Vector3(0, -9.8f, 0), 30, 19);
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
    

    protected void suspendRendering () {
        renderTask.suspend();
    }

    protected void resumeRendering () {
        renderTask.resume();
    }
    
    private class RenderTask implements SchedTask {
        @Override
        public void onScheduled(SchedTime time) {
            renderControl.requestAndWaitForRender();
        }

        @Override
        public void beginSchedule(TaskController t) {
        }

        @Override
        public void endSchedule(TaskController t) {
        }
    }
    
    private RenderControl renderControl;
    private TaskController renderTask = null;
    private void internalPreparation () {
        this.modelBatch = new ModelBatch();
        this.first = true;
        
        long period = config.itemRenderingPeriod;
        renderTask = schedule(0, period, new RenderTask());
        suspendRendering();
    }
    private void internalPreRender () {
        if (first) {
            renderControl = new RenderControl(this);
            resumeRendering();
            schedUpdater.start();
            first = false;
        }
    }
    
    boolean advance (boolean endScene) {
        switch (getSceneState()) {
        case Preparing:
            clearBuffers();
            if (first) {
                internalPreparation ();
                prepare ();
                first = false;
            }
            nextState = SceneState.InitResource;
            // NOTE: intended falling-through
        case InitResource:
            clearBuffers();
            doInitResource();
            break;
        case Running:
            internalPreRender();
            if (!endScene) {
                renderControl.renderScene();
                break;
            } else {
                nextState = SceneState.TearingDown;
                // NOTE: intended falling-through
            }
        case TearingDown:
            clearBuffers();
            internalPreTeardown();
            tearDown();
            destroy();
            nextState = SceneState.ChangingScene;
            break;
        default:
            break;
        }
        if (state != nextState) {
            state = nextState;
            first = true;
        }
        return state != SceneState.ChangingScene;
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
        Environment env;
        // core -> ItemSpace
        // server -> item
        // Where is eden supposed to be located?
        //
        // Draw Using ShaderedBatch
        // Draw Using DecalBatch
        // Draw Using SpriteBatch 
        //
        //
        
        if (core.getCompMgrSpace().has(CameraServer.class)) {
            modelBatch.begin(core.getCompMgr(CameraServer.class).getCamera());
            env = core.getCompMgr(EnvServer.class).makeEnvironment();
            core.getCompMgr(Render3DServer.class).render(modelBatch, env);
            modelBatch.end();
        }
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
            for (BaseCompMgr server : itemReservable.reservedServers()) {
                core.addCompMgr(server);
            }
            core.addItems(itemReservable.reservedItems());
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
    
    void internalPreTeardown () {
        schedUpdater.stop();
        scheduler.killAll();
    }

    public abstract void prepare ();
    public abstract void postRender ();
    public abstract void resize (int width, int height);
    public abstract void pause ();
    public abstract void resume ();

    protected void tearDown () { }
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
    
    public Scheduler getScheduler () {
        return scheduler;
    }
    
    public GameEventMgr getGameEventMgr () {
        return gameEventMgr;
    }
    
    public SceneMgr getSceneMgr () {
        return manager;
    }

    
    public TaskController schedule(long delay, long period, SchedTask task) {
        return this.scheduler.schedule(delay, period, task);
    }

    public TaskController scheduleOnce(long delay, long relativeDeadline,
            SchedTask task) {
        return this.scheduler.scheduleOnce(delay, relativeDeadline, task);
    }

    public TaskController schedule(long delay, long period, SchedTask task,
            SchedTaskConfig config) {
        return this.scheduler.schedule(delay, period, task, config);
    }

    public TaskController scheduleOnce(long delay, long relDeadline,
            SchedTask task, SchedTaskConfig config) {
        return this.scheduler.scheduleOnce(delay, relDeadline, task, config);
    }
}