package com.algy.schedcore.middleend;

import java.util.HashMap;
import java.util.Iterator;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.Core;
import com.algy.schedcore.ICore;
import com.algy.schedcore.ITickGetter;
import com.algy.schedcore.Item;
import com.algy.schedcore.NameConflictError;

public class GameCore extends Core implements Iterable<GameItem> {
    private HashMap<String, GameItem> itemNameMap = new HashMap<String, GameItem>();
    

    private GameItem headItem = null, 
                     tailItem = null;

    public GameCore(ITickGetter tickGetter) {
        super(tickGetter);
    }


    @Override
    public void addItemAll(Iterable<Item<BaseComp, ICore>> itemIterable) {
        for (Item<BaseComp, ICore> item : itemIterable) {
            hookItem(item);
        }
        super.addItemAll(itemIterable);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addGameItemAll(Iterable<GameItem> itemIterable) {
        // HACK: We cannot cast Iterable<Child> into Iterable<Parent> in Java,
        //       when Child extends Parent.
        //       So, Here are some hacky stuffs to force it.
        Iterable childIterable = itemIterable;
        Iterable<Item<BaseComp, ICore>> parentIterable = childIterable;
        this.addItemAll(parentIterable);
    }
    
    @Override
    public void addItem(Item<BaseComp, ICore> item, boolean suspend) {
        hookItem(item);
        super.addItem(item, suspend);
    }
    
    @Override
    public void addItem(Item<BaseComp, ICore> item) {
        hookItem(item);
        super.addItem(item);
    }
    
    void updateNameMap (GameItem item, String oldName) {
        if (oldName != null)
            unregName(oldName);
        String curName = item.getName();
        if (curName != null)
            regName(curName, item);
    }

    @Override
    public void removeItem(Item<BaseComp, ICore> item) {
        GameItem gameItem = (GameItem) item;
        if (gameItem.getName() != null)
            unregName(gameItem.getName());

        if (gameItem.prev != null)
            gameItem.prev.next = gameItem.next;
        else
            headItem = gameItem.next;
        if (gameItem.next != null)
            gameItem.next.prev = gameItem.prev;
        else
            tailItem = gameItem.prev;
        super.removeItem(item);
        gameItem.removeAll();
    }
    
    public void clearAll() {
        GameItem next;
        for (GameItem p = headItem; p != null; p = next) {
            next = p.next;
            removeItem(p);
        }
        removeServer(BaseCompServer.class);
        headItem = tailItem = null;
    }
    
    public boolean hasItemWithName(String name) {
        return itemNameMap.containsKey(name);
    }
    
    public GameItem getItemWithName (String name) {
        return itemNameMap.get(name);
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
    
    private void regName (String itemName, GameItem gameItem) {
        assertName(itemName, false);
        itemNameMap.put(itemName, gameItem);
    }
    private void unregName (String itemName) {
        assertName(itemName, true);
        itemNameMap.remove(itemName);
    }
    
    private void assertName (String name, boolean assertContainingOrNot) {
        if (itemNameMap.containsKey(name) ^ assertContainingOrNot) {
            if (assertContainingOrNot)
                throw new NameConflictError("GameItem with name '" + name + "' doesn't exist");
            else
                throw new NameConflictError("GameItem with name '" + name + "' already exist");

        }
    }
    
    private void hookItem (Item<BaseComp, ICore> item) {
        GameItem gameItem = (GameItem) item;
        updateNameMap(gameItem, null);
        
        if (tailItem == null) 
            tailItem = headItem = gameItem;
        else {
            gameItem.prev = tailItem;
            gameItem.next = null;
            tailItem.next = gameItem;
            tailItem = gameItem;
        }
    }
}