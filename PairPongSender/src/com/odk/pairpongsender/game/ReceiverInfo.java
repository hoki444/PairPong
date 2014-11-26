package com.odk.pairpongsender.game;

import java.util.UUID;

public class ReceiverInfo {
    public String uuid;
    public int duration;
    
    public ReceiverInfo () {
    }

    public ReceiverInfo (int duration) {
        this.uuid = UUID.randomUUID().toString();
        this.duration = duration;
    }
}
