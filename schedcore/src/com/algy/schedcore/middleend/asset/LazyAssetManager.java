package com.algy.schedcore.middleend.asset;

import com.algy.schedcore.SchedcoreRuntimeError;
import com.algy.schedcore.util.Promise;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;


public class LazyAssetManager implements Disposable, AssetProvider {
    private AssetManager fileAssets;
    private AssetList usedAssets = new AssetList();
    private boolean allowOnTheFlyLoading = false;
    
    public LazyAssetManager () {
        this.fileAssets = new AssetManager();
    }

    public LazyAssetManager (FileHandleResolver fileResolver) {
        this.fileAssets = new AssetManager(fileResolver);
    }
    
    public synchronized boolean isOnTheFlyLoadingEnabled () {
        return allowOnTheFlyLoading;
    }
    
    public synchronized void enableOnTheFlyLoading (boolean enable) {
        this.allowOnTheFlyLoading = enable;
    }

    public AssetLoadingController load (AssetList assetList) {
        for (AssetSig sig : assetList) {
            if (!fileAssets.isLoaded(sig.assetName, sig.assetClass)) {
                synchronized (usedAssets) {
                    usedAssets.add(sig);
                }
                fileAssets.load(sig.assetName, sig.assetClass);
            }
        }
        return new AssetLoadingController() {
            boolean finished = false;
            @Override
            public void update(int millis) {
                if (millis <= 0) {
                    finished = fileAssets.update();
                } else {
                    finished = fileAssets.update(millis);
                }
            }
            @Override
            public float progress() {
                return fileAssets.getProgress();
            }
            
            @Override
            public boolean isFinished() {
                return finished;
            }

            @Override
            public void join () {
                fileAssets.finishLoading();
                finished = true;
            }
        };
    }
    
    public <T> T loadImmediately (String fileName, Class<T> type) {
        if (fileAssets.isLoaded(fileName, type))
            return fileAssets.get(fileName, type);
        else {
            synchronized (usedAssets) {
                usedAssets.add(new AssetSig(fileName, type));
            }
            fileAssets.load(fileName, type);
            // XXX: long blocking may occur if other threads is loading assets using this manager at the same time.
            //      , though that is unlikely to happen due to single-threaded nature of Schedcore.
            fileAssets.finishLoading();
            return fileAssets.get(fileName, type);
        }
    }
    public boolean isLoaded(String fileName) {
        return fileAssets.isLoaded(fileName);
    }
    
    public synchronized void unloadOthers (AssetList assetsToBeUsed) {
        for (AssetSig uselessAssetSig : usedAssets.difference(assetsToBeUsed)) {
            unload(uselessAssetSig.assetName);
        }
    }
    
    public synchronized void unload (String fileName) {
        this.usedAssets.remove(fileName);
        fileAssets.unload(fileName);
    }

    public void unloadAll () {
        for (String assetName : usedAssets.assetNames()) {
            unload(assetName);
        }
    }
    
    public <T> Promise<T> promiseAsset (final String fileName, final Class<T> type) {
        return new Promise<T>() {
            @Override
            public T get() {
                if (isLoaded(fileName)) {
                    return fileAssets.get(fileName, type);
                } else if (allowOnTheFlyLoading) {
                    getLogger().info("[On-the-fly loading] " + fileName);
                    return loadImmediately(fileName, type);
                } else
                    throw new SchedcoreRuntimeError("Asset not loaded: " + fileName);
            }
        };
    }
    
    @Override
    public <T> T get (String fileName, Class<T> type) {
        return this.fileAssets.get(fileName, type);
    }

    public <T, P extends AssetLoaderParameters<T>> void setLoader(
            Class<T> type, AssetLoader<T, P> loader) {
        this.fileAssets.setLoader(type, loader);
    }

    public <T, P extends AssetLoaderParameters<T>> void setLoader(
            Class<T> type, String suffix, AssetLoader<T, P> loader) {
        this.fileAssets.setLoader(type, suffix, loader);
    }

    public void setErrorListener(AssetErrorListener listener) {
        this.fileAssets.setErrorListener(listener);
    }

    @Override
    public void dispose() {
        this.fileAssets.dispose();
    }

    public String getDiagnostics() {
        return this.fileAssets.getDiagnostics();
    }

    public Array<String> getAssetNames() {
        return this.fileAssets.getAssetNames();
    }

    public Array<String> getDependencies(String fileName) {
        return this.fileAssets.getDependencies(fileName);
    }

    
    public Logger getLogger () {
        return this.fileAssets.getLogger();
    }
    public void setLogger(Logger logger) {
        this.fileAssets.setLogger(logger);
    }


    public Class<?> getAssetType(String fileName) {
        return this.fileAssets.getAssetType(fileName);
    }
}