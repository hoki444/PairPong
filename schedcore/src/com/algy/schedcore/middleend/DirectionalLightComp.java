package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

public class DirectionalLightComp extends LightComp {
    private DirectionalLight light;
    
    public DirectionalLightComp (Vector3 direction) {
        this.light = new DirectionalLight();
        light.direction.set(direction);
    }
    
    private DirectionalLightComp (DirectionalLight light) {
        this.light = light;
    }
    
    public void setDirection (Vector3 direction) {
        light.direction.set(direction);
    }
    
    public Vector3 getDirection () {
        return light.direction;
    }

    @Override
    public BaseComp duplicate() {
        return new DirectionalLightComp(new DirectionalLight().set(light));
    }

    @Override
    public BaseLight getLight() {
        return light;
    }

    @Override
    protected void onAttached() {
    }

    @Override
    protected void onDetached() {
    }

    @Override
    public LightComp setColor(float r, float g, float b, float a) {
        this.light.color.set(r, g, b, a);
        return this;
    }

    @Override
    public LightComp setColor(Color color) {
        this.light.color.set(color);
        return this;
    }
}
