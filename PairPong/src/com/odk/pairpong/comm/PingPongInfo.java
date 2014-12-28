package com.odk.pairpong.comm;

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
