package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.GameItem;
import com.badlogic.gdx.math.Vector3;

public abstract class CollisionComp extends BaseComp {
    static public interface CollisionInfo {
        public float getImpulse();
        public void thisPosition(Vector3 posOut);
        public void otherPosition(Vector3 posOut);
        public void thisLocalPosition(Vector3 posOut);
        public void otherLocalPosition(Vector3 posOut);
    }
    
    public abstract void beginCollision(GameItem other);
    public abstract void endCollision(GameItem other, Iterable<CollisionInfo> info);
}
