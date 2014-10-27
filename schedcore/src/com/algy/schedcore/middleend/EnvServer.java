package com.algy.schedcore.middleend;

import java.util.ArrayList;
import java.util.List;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.IntegerBitmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class EnvServer extends BaseCompServer {
    private IntegerBitmap<LightComp> lightBitmap = new IntegerBitmap<LightComp>(8);
    public Color ambientLightColor = new Color(0, 0, 0, 0);
    
    @Override
    public List<Class<? extends BaseComp>> hookFilters() {
        ArrayList<Class<? extends BaseComp>> result = new ArrayList<Class<? extends BaseComp>>();
        result.add(LightComp.class);
        return result;
    }
    
    public Environment makeEnvironment() {
        Environment env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLightColor));
        for (LightComp lightComp : lightBitmap)
            env.add(lightComp.getLight());
        return env;
    }

    @Override
    public void hookAddComp(BaseComp comp) {
        LightComp lcomp = (LightComp)comp;
        int id = lightBitmap.add(lcomp);
        lcomp.lightId = id;
    }

    @Override
    public void hookRemoveComp(BaseComp comp) {
        LightComp lcomp = (LightComp)comp;
        lightBitmap.remove(lcomp.lightId);
        lcomp.lightId = -1;
    }

    @Override
    protected void onAdhered() {
    }

    @Override
    protected void onDetached() {
    }

}
