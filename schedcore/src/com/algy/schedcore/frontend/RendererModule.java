package com.algy.schedcore.frontend;

import com.algy.schedcore.GameItemSpace;

public interface RendererModule <BATCH> {
    public void beginRendering (GameItemSpace core);
    public void render(BATCH batch);
    public void endRendering(GameItemSpace core);
}