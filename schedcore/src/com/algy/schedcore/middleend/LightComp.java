package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;

public abstract class LightComp extends BaseComp {
    public abstract LightComp setColor (float r, float g, float b, float a);
    public abstract LightComp setColor (Color color);
    public abstract BaseLight getLight ();
    
    public int lightId = -1;
}