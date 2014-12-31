package com.algy.schedcore.frontend;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.GameItem;

public interface ItemReservable {

    public void reserveServer(BaseCompMgr server);
    public void reserveServer(Iterable<BaseCompMgr> servers);
    public void reserveItem(GameItem gameItem);
    public void reserveItem(Iterable<GameItem> gameItems);

    public Iterable<GameItem> reservedItems();
    public Iterable<BaseCompMgr> reservedServers();
}