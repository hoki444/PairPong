package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputPushServer extends BaseCompMgr {
    private InputMultiplexer multiplexer = new InputMultiplexer();

    @Override
    protected void onAttached() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    protected void onDetached() {
    }


    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> sigs) {
        sigs.add(InputComp.class);
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        multiplexer.addProcessor((InputProcessor) comp);
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        multiplexer.removeProcessor((InputProcessor) comp);
    }
}