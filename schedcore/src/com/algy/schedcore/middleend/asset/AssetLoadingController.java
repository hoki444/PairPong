package com.algy.schedcore.middleend.asset;

public interface AssetLoadingController {
    public void update (int millis);
    public void join ();
    public float progress ();
    public boolean isFinished();
}