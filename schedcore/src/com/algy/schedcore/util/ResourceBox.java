package com.algy.schedcore.util;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;

public class ResourceBox <T extends Disposable> {
    private T resource;
    private int refCount = 0;

    public ResourceBox (T res) {
        this.resource = res;
    }

    public T get () {
        return resource;
    }
    
    public ResourceBox<T> obtain () {
        refCount++;
        return this;
    }
    
    public void release () {
        refCount--;
        if (refCount <= 0) {
            resource.dispose();
        }
    }
    
    public int getRefCount () {
        return refCount;
    }
    
    public static ResourceBox<Texture> textureBox (Texture tex) {
        return new ResourceBox<Texture>(tex);
    }
    
    public static ResourceBox<Model> modelBox (Model model) {
        return new ResourceBox<Model>(model);
    }
}