package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.MutableLister;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputPushServer extends BaseCompServer {
    private InputMultiplexer multiplexer = new InputMultiplexer();

    @Override
    protected void onAdhered() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    protected void onDetached() {
    }


    @Override
    public void hookFilters(MutableLister<Class<? extends BaseComp>> sigs) {
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