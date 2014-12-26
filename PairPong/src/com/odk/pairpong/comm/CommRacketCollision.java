package com.odk.pairpong.comm;

import java.util.UUID;

public class CommRacketCollision {
    public String uuid;
    public int duration;
    public CommRacketCollision () {
    }
    public CommRacketCollision (int duration) {
        this.uuid = UUID.randomUUID().toString();
        this.duration = duration;
    }
}
