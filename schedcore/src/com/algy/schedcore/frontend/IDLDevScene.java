package com.algy.schedcore.frontend;

import java.util.ArrayList;
import java.util.HashSet;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.BaseSchedServer;
import com.algy.schedcore.IComp;
import com.algy.schedcore.frontend.idl.IDLError;
import com.algy.schedcore.frontend.idl.IDLLoader;
import com.algy.schedcore.frontend.idl.IDLResult;
import com.algy.schedcore.middleend.GameCore;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.InputComp;
import com.algy.schedcore.middleend.asset.AssetList;
import com.algy.schedcore.middleend.asset.AssetLoadingController;
import com.algy.schedcore.util.FileObserver;
import com.algy.schedcore.util.ObjectDirectory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;


public class IDLDevScene extends Scene {
    private BitmapFont bitmapFont;
    private SpriteBatch spriteBatch;

    private String errorMsg;
    private int errorRem = 0;
    private static int ERROR_DURATION = 400;
    private Object errorLock = new Object();
    
    private String infoMsg;
    private int infoRem = 0;
    private static int INFO_DURATION = 30;
    private Object infoLock = new Object();
    

    private FileObserver fileObserver = new FileObserver(observingPeriod());
    private Boolean reloadingRequired = false;
    private HashSet<FileHandle> destModFiles;
    private int sceneIdxStart, sceneIdxEnd;
    private int modIdxStart, modIdxEnd;
    private int defIdxStart, defIdxEnd;
    private FileHandle [] fileArray;
    
    private synchronized void initObserverState () {
        destModFiles = new HashSet<FileHandle>();
        reloadingRequired = false;
    }
    
    private void setInfo (String msg) {
        synchronized (infoLock) {
            infoMsg = msg;
            infoRem = INFO_DURATION;
        }
    }

    private void setError (String msg) {
        synchronized (errorLock) {
            errorMsg = msg;
            errorRem = ERROR_DURATION;
        }
    }
    
    private void addDefaultDevItems (ItemReservable reservable) {
        GameItem stopTheWorldItem = new GameItem();
        stopTheWorldItem.add(new InputComp() {
            @Override
            public IComp duplicate() {
                return null;
            }
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }
            
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
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
                return false;
            }
            
            @Override
            public boolean keyTyped(char character) {
                return false;
            }
            
            private boolean stop = true;
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    reloadingRequired = true;
                } else if (keycode == Input.Keys.SPACE) {
                    GameCore core = (GameCore)core();
                    if (stop) {
                        for (GameItem gameItem : core) {
                            core.suspendItem(gameItem);
                        }
                        for (BaseCompServer server : core.servers()) {
                            if (server instanceof BaseSchedServer) {
                                ((BaseSchedServer)server).getTask().suspend();
                            }
                        }
                        setInfo("[Core Suspended]");
                        stop = false;
                    } else {
                        for (GameItem gameItem : core) {
                            core.resumeItem(gameItem);
                        }
                        for (BaseCompServer server : core.servers()) {
                            if (server instanceof BaseSchedServer) {
                                ((BaseSchedServer)server).getTask().resume();
                            }
                        }
                        setInfo("[Core Resumed]");
                        stop = true;
                    }
                }
                return false;
            }
        });
        reservable.reserveItem(stopTheWorldItem);
    }

    public int observingPeriod() {
        return 500;
    }
    
    protected FileHandle [] defFiles () {
        return new FileHandle [] {Gdx.files.external("Documents/Defs")};
    }
    
    protected FileHandle [] sceneFiles () {
        return new FileHandle [] {Gdx.files.external("Documents/Scene")};
    }
    
    protected FileHandle [] modFiles () {
        return new FileHandle [] {Gdx.files.external("Documents/Mod")};
    }

    @Override
    public void postRender() {
        spriteBatch.begin();
        if (errorRem > 0) {
            bitmapFont.setColor(0.8f, 0.8f, 0, 1);
            bitmapFont.drawWrapped(spriteBatch, errorMsg, 0, Gdx.graphics.getHeight() - 15, 
                                     Gdx.graphics.getWidth(), HAlignment.LEFT);
            errorRem--;
        }
        if (infoRem > 0) {
            bitmapFont.setColor(.8f, .8f, .8f, 1);
            bitmapFont.draw(spriteBatch, infoMsg, 15, 30);
            infoRem--;
        }
        spriteBatch.end();

        if (reloadingRequired) {
            this.core().clearAll();
            setInfo("Reloading...");
            this.initializeResource(new SceneResourceInitializer() {
                @Override
                public void reserveItem(Scene scene, ItemReservable reservable) {
                    addDefaultDevItems(reservable);
                    for (int idx = defIdxStart; idx < defIdxEnd; idx++) {
                        FileHandle fh = null;
                        try {
                            fh = fileArray[idx];
                            if (fh.exists()) {
                                String source = fh.readString();
                                ObjectDirectory<GameItem> dir = 
                                    IDLLoader.loadItemDef(source, IDLDevScene.this).newItemDef;
                                eden().update(dir);
                            }
                        } catch (IDLError e) {
                            setError("[" + fh + "]" + e.getMessage());
                        }
                    }
                    
                    for (int idx = sceneIdxStart; idx < sceneIdxEnd; idx++) {
                        FileHandle fh = null;
                        try {
                            fh = fileArray[idx];
                            if (fh.exists()) {
                                IDLResult result = 
                                        IDLLoader.loadScene(fh.readString(), IDLDevScene.this);
                                reservable.reserveServer(result.createdServers);
                                reservable.reserveItem(result.createdItems);
                            }
                        } catch (IDLError e) {
                            setError("[" + fh + "]" + e.getMessage());
                        }
                    }
                    scene.Done (); 
                }

                @Override
                public void loadAsset(Scene scene, AssetLoadingController controller,
                        AssetList newAssetList) {
                    controller.join();
                    scene.Done ();
                }
                
                @Override
                public void gatherAsset(Scene scene, AssetGathererProxy gatherer) {
                    scene.Done();
                }
                
                @Override
                public void endResourceInitialization(Scene scene) {
                    initObserverState ();
                    scene.Done();
                }
                
                @Override
                public void beginResourceInitialization(Scene scene) {
                    scene.Done();
                }
            });
        } else if (!destModFiles.isEmpty()) {
            ArrayList<FileHandle> mods = null;
            synchronized (destModFiles) {
                if (!destModFiles.isEmpty()) {
                    mods = new ArrayList<FileHandle>(destModFiles);
                    destModFiles.clear();
                }
            }

            if (mods != null && !mods.isEmpty()) {
                for (FileHandle modFile : mods) {
                    try {
                        IDLResult result = IDLLoader.modifyScene(modFile.readString(), this);
                        assetManager().load(result.requiredAssetList).join();
                    } catch (IDLError e) {
                        setError("[" + modFile.name() + "]" + e.getMessage());
                    }
                }
            }
        }
    }
    
    private FileObserver.Notification notification = new FileObserver.Notification() {
        @Override
        public void fileRemoved(FileHandle file, int index) {
            setInfo("[File] '" + file.name() + "' has been removed");
        }

        @Override
        public void fileModified(FileHandle file, int index) {
            setInfo("[File] '" + file.name() + "' has been modified");
            if (modIdxStart <= index && modIdxEnd > index) {
                synchronized (destModFiles) {
                    destModFiles.add(file);
                }
            } else {
                reloadingRequired = true;
            }
        }
        @Override
        public void fileCreated(FileHandle file, int index) {
            setInfo("[File] '" + file.name() + "' has been created");
            if (modIdxStart <= index && modIdxEnd > index) {
                synchronized (destModFiles) {
                    destModFiles.add(file);
                }
            } else {
                reloadingRequired = true;
            }
        }
    };


    @Override
    public void prepare() {
        bitmapFont = new BitmapFont();
        bitmapFont.setScale(1.1f);
        spriteBatch = new SpriteBatch();
        this.assetManager().enableOnTheFlyLoading(true);
        initObserverState();

        FileHandle [] def = defFiles ();
        FileHandle [] scene = sceneFiles ();
        FileHandle [] mod = modFiles ();
        defIdxStart = 0;
        defIdxEnd = def.length;
        sceneIdxStart = defIdxEnd;
        sceneIdxEnd = sceneIdxStart + scene.length;
        modIdxStart = sceneIdxEnd;
        modIdxEnd = sceneIdxEnd + mod.length;
        fileArray = new FileHandle [modIdxEnd];
        
        for (int idx = defIdxStart; idx < defIdxEnd; idx++) {
            fileArray[idx] = def[idx - defIdxStart];
        }

        for (int idx = sceneIdxStart; idx < sceneIdxEnd; idx++) {
            fileArray[idx] = scene[idx - sceneIdxStart];
        }

        for (int idx = modIdxStart; idx < modIdxEnd; idx++) {
            fileArray[idx] = mod[idx - modIdxStart];
        }

        fileObserver.register(notification);
        fileObserver.start(fileArray);
    }

    @Override
    public void tearDown() {
        fileObserver.end();
        bitmapFont.dispose();
        spriteBatch.dispose();
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
