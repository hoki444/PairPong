package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.GameItemSpace;
import com.algy.schedcore.middleend.Eden;

public interface IDLGameContext {
    public Eden eden();
    public GameItemSpace core();
}