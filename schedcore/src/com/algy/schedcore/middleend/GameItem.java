package com.algy.schedcore.middleend;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.ICore;
import com.algy.schedcore.Item;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GameItem extends Item<BaseComp, ICore> {
    // default component
    /*
     * Transform
     * 
     */
    public GameItem() {
        super(BaseComp.class);
        this.add(new Transform(0, 0, 0)); 
    }

    private GameItem(Transform transform) {
        super(BaseComp.class);

        this.add(transform);
    }

    public GameItem duplicate (Vector3 pos, Quaternion ori, Vector3 scale) {
        GameItem newItem = new GameItem(new Transform(pos, ori, scale));


        for (BaseComp comp : this) {
            if (!(comp instanceof Transform)) {
                newItem.add((BaseComp)comp.duplicate());
            }
        }
        return newItem;
    }
    
    public GameItem duplicate (Vector3 pos, Quaternion ori) {
        return duplicate (pos, ori, new Vector3(1, 1, 1));
    }

    public GameItem duplicate (Vector3 pos) {
        return duplicate (pos, new Quaternion());
    }
    
    public GameItem duplicate () {
        return duplicate(new Vector3());

    }
    
    public Transform getTransform() {
        return this.as(Transform.class);
    }
}
