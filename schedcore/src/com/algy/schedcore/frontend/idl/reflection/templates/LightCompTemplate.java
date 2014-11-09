package com.algy.schedcore.frontend.idl.reflection.templates;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.RequiredFields;
import com.algy.schedcore.frontend.idl.reflection.SelectiveGroup;
import com.algy.schedcore.middleend.DirectionalLightComp;
import com.algy.schedcore.middleend.LightComp;
import com.algy.schedcore.middleend.PointLightComp;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class LightCompTemplate extends IDLCompTemplate {
    private static class Directional {
        RequiredFields req = new RequiredFields("direction");
        public Vector3 direction = null;
    }
    private static class Point {
        RequiredFields req = new RequiredFields("intensity");
        public Float intensity = null;
    }
    RequiredFields req = new RequiredFields("color");
    SelectiveGroup sel = new SelectiveGroup("directional", "point");

    public Color color = null;

    public Directional directional = null;
    public Point point = null;
    
    @Override
    protected BaseComp create(IDLGameContext context) {
        LightComp result;
        if (directional != null) {
            result = new DirectionalLightComp(directional.direction);
        } else { //if (point != null) {
            result = new PointLightComp(point.intensity);
        }
        result.setColor(color);
        return result;
    }

    @Override
    protected void modify(IDLGameContext context, BaseComp comp) {
        LightComp lightComp = (LightComp)comp;
        
        if (point != null) {
            if (lightComp instanceof PointLightComp) { 
                if (point.intensity != null)
                    ((PointLightComp)lightComp).setIntensity(point.intensity);
            } else
                keyNotModifiableError("point");
        } else if (directional != null) {
            if (lightComp instanceof DirectionalLightComp) {
                if (directional.direction != null)
                    ((DirectionalLightComp)lightComp).setDirection(directional.direction);
            } else
                keyNotModifiableError("directional");
        }
        if (color != null) {
            lightComp.setColor(color);
        }
    }

}