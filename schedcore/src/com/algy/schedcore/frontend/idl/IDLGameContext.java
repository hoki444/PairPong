package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.middleend.Eden;
import com.algy.schedcore.middleend.GameCore;

public interface IDLGameContext {
    public Eden eden();
    public GameCore core();
}