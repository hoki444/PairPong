package com.algy.schedcore.frontend;

import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.middleend.GameItem;

public interface ItemReservable {

    public void reserveServer(BaseCompServer server);
    public void reserveServer(Iterable<BaseCompServer> servers);
    public void reserveItem(GameItem gameItem);
    public void reserveItem(Iterable<GameItem> gameItems);

    public Iterable<GameItem> reservedItems();
    public Iterable<BaseCompServer> reservedServers();
}