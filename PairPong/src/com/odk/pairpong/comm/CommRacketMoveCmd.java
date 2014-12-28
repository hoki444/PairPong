package com.odk.pairpong.comm;

import java.util.UUID;


public class CommRacketMoveCmd {
    public String uuid;
    public float posX, posY;
    public float theta;
    
    public CommRacketMoveCmd () {
    }
    
    public CommRacketMoveCmd (float posX, float posY, float theta) {
        this.uuid = UUID.randomUUID().toString();
        this.posX = posX;
        this.posY = posY;
        this.theta = theta;
    }
}