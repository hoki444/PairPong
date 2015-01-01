package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.GameItem;
import com.algy.schedcore.frontend.ItemReservable;
import com.algy.schedcore.frontend.Scene;
import com.algy.schedcore.middleend.EnvServer;
import com.algy.schedcore.util.ObjectDirectory;
import com.badlogic.gdx.Gdx;

public class IDLTestScene extends Scene {
    @Override
    public void postRender() {
    }

    @Override
    public void prepare() {

        ObjectDirectory<GameItem> dir = 
                IDLLoader.loadItemDef(Gdx.files.internal("Defs").readString(), this).newItemDef;
        eden().update(dir);
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

    @Override
    public void reserveItem(Scene scene, ItemReservable reservable) {
        IDLResult result = IDLLoader.loadScene(Gdx.files.internal("Scene").readString(), this);
        reservable.reserveServer(result.createdServers);
        reservable.reserveItem(result.createdItems);
        Done ();
    }

    @Override
    public void endResourceInitialization(Scene scene) {
        core.getCompMgr(EnvServer.class).ambientLightColor.set(.2f, .2f, .2f, 1); 
        Done ();
    }
}