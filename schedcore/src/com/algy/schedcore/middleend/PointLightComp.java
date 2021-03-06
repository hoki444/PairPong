package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

public class PointLightComp extends LightComp {
    private PointLight pointLight;
    public PointLightComp (float intensity) {
        this.pointLight = new PointLight();
        pointLight.intensity = intensity;
    }

    private PointLightComp (PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public void setIntensity(float intensity) {
        pointLight.intensity = intensity;
    }

    @Override
    public BaseComp duplicate() {
        return new PointLightComp(new PointLight().set(pointLight));
    }

    @Override
    public LightComp setColor(float r, float g, float b, float a) {
        pointLight.color.set(r, g, b, a);
        return this;
    }

    @Override
    public LightComp setColor(Color color) {
        pointLight.color.set(color);
        return this;
    }

    @Override
    public BaseLight getLight() {
        Transform tr = owner().as(Transform.class);
        tr.getTranslation(this.pointLight.position);
        return pointLight;
    }

    @Override
    protected void onAttached() {
    }

    @Override
    protected void onDetached() {
    }
    
}
