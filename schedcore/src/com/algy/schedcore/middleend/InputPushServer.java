package com.algy.schedcore.middleend;

import java.util.ArrayList;
import java.util.List;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
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
    public List<Class<? extends BaseComp>> hookFilters() {
        ArrayList<Class<? extends BaseComp>> res = new ArrayList<Class<? extends BaseComp>>();
        res.add(InputComp.class);
        return res;
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