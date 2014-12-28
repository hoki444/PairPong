package com.odk.pairpong.comm.game;

import java.util.UUID;

public class PingPongInfo {
    public String uuid;
    public long timestamp;
    public PingResult resultListener;
    public PingPongInfo () {
        uuid = UUID.randomUUID().toString();
        timestamp = System.currentTimeMillis();
    }
}
