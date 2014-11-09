package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.util.IntegerBitmap;
import com.algy.schedcore.util.Lister;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class EnvServer extends BaseCompServer {
    private IntegerBitmap<LightComp> lightBitmap = new IntegerBitmap<LightComp>(8);
    public Color ambientLightColor = new Color(0, 0, 0, 0);
    
    @Override
    public void listCompSignatures(Lister<Class<? extends BaseComp>> sigs) {
        sigs.add(LightComp.class);
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


    public Color getAmbientLightColor() {
        return this.ambientLightColor;
    }

    public void setAmbientLightColor(Color ambientLightColor) {
        this.ambientLightColor = ambientLightColor;
    }
}