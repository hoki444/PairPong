package com.odk.pairpongsender.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class GThetaProvider {
    private static float Alpha = 0.5f;
    private float old;
    private boolean isFirst;
    public GThetaProvider ( ) {
        isFirst = true;
    }
    public float obtainTheta() {
        if (isFirst) {
            float theta = getCur();

            old = theta * Alpha + old * (1 - Alpha);
            return old;
        } else {
            old = getCur ();
            return old;
        }
    }
    private float getCur() {
        float x, y, z, l;
        x = Gdx.input.getAccelerometerX();
        y = Gdx.input.getAccelerometerY();
        z = Gdx.input.getAccelerometerZ();
        l = (float)Math.sqrt(x * x + y * y + z * z);
        return (float)Math.toDegrees(Math.acos(z/l));
    }
}
