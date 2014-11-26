package com.odk.pairpong.game;

import java.util.UUID;


public class SenderInfo {
    public String uuid;
    public float posX, posY;
    public float theta;
    
    public SenderInfo () {
    }
    
    public SenderInfo (float posX, float posY, float theta) {
        this.uuid = UUID.randomUUID().toString();
        this.posX = posX;
        this.posY = posY;
        this.theta = theta;
    }
    
}