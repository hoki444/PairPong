package com.algy.schedcore.middleend;

import java.util.Iterator;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.Core;
import com.algy.schedcore.ICore;
import com.algy.schedcore.ITickGetter;
import com.algy.schedcore.Item;

public class GameCore extends Core implements Iterable<GameItem> {
    private GameItem headItem = null, 
                     tailItem = null;

    public GameCore(ITickGetter tickGetter) {
        super(tickGetter);
    }

    @Override
    public void addItem(Item<BaseComp, ICore> item) {
        super.addItem(item);
        GameItem gameItem = (GameItem) item;

        if (tailItem == null) 
            tailItem = headItem = gameItem;
        else {
            gameItem.prev = tailItem;
            gameItem.next = null;
            tailItem.next = gameItem;
            tailItem = gameItem;
        }
    }

    @Override
    public void removeItem(Item<BaseComp, ICore> item) {
        super.removeItem(item);
        GameItem gameItem = (GameItem) item;
        if (gameItem.prev != null)
            gameItem.prev.next = gameItem.next;
        else
            headItem = gameItem.next;
        if (gameItem.next != null)
            gameItem.next.prev = gameItem.prev;
        else
            tailItem = gameItem.prev;
        gameItem.removeAll();
    }
    
    public void clearAll() {
        GameItem next;
        for (GameItem p = headItem; p != null; p = next) {
            next = p.next;
            p.removeAll();
        }
        removeServer(BaseCompServer.class);
        headItem = tailItem = null;
    }

    @Override
    public Iterator<GameItem> iterator() {
        return new Iterator<GameItem>() {
            private GameItem iterItem = headItem;
            @Override
            public boolean hasNext() {
                return iterItem != null;
            }

            @Override
            public GameItem next() {
                GameItem result = iterItem;
                iterItem = iterItem.next;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
