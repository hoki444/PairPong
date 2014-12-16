package com.odk.pairpong.game;

public class StateInterpolater {
    private float state;
    private float destState;
    private float xclampmin, xclampmax;
    private float vmax, vmin;

    public StateInterpolater (float xclampmin, float xclampmax, float vmin, float vmax) {
        this.state = 0;
        this.vmin = vmin;
        this.vmax = vmax;
        this.xclampmax = xclampmax;
        this.xclampmin = xclampmin;
    }
    
    
    public void setState(float state) {
        this.state = state;
    }
    
    public void setDestState (float destState ) {
        this.destState = destState;
    }
    
    public float getState() {
        return state;
    }
    
    public float getDestState() {
        return destState;
    }


    public void update (float dt) {
        state += getVelocity() * dt;
    }
    
    public float getVelocity() {
        float v;
        if (state > destState) {
            v = -smoothstep(xclampmin, xclampmax, state - destState, vmin, vmax);
        } else if (state < destState) {
            v = smoothstep(xclampmin, xclampmax, destState - state, vmin, vmax);
        } else
            v = 0;
        return v;
    }

    public static float smoothstep(float xedge0, float xedge1, float x, float vmin, float vmax)
    {
        // Reference: http://en.wikipedia.org/wiki/Smoothstep
        // Scale, bias and saturate x to 0..1 range
        x = (x - xedge0)/(xedge1 - xedge0);
        x = x < 0? 0 : x;
        x = x > 1? 1 : x;

        // Evaluate polynomial
        return (vmax - vmin) * x*x*(3 - 2*x) + vmin;
    }

    public static float clamp(float x, float xmin, float xmax) {
        x = x < xmin? xmin : x;
        x = x > xmax? xmax : x;

        return x;
    }
}
