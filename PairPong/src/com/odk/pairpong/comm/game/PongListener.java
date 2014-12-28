package com.odk.pairpong.comm.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.odk.pairpong.comm.general.CommFunction;

public class PongListener extends PingPongListener {
    private HashMap<String, PingPongInfo> pendings = new HashMap<String, PingPongInfo>();
    private int timelimit;
    public PongListener(CommFunction commFun, int timelimit) {
        super(commFun);
        this.timelimit = timelimit;
    }

    @Override
    public String getTypeName() {
        return CommConstants.TYPE_PONG;
    }

    @Override
    public void onReceive(String receivedUUID) {
        PingPongInfo info = null;
        synchronized (pendings) {
            info = pendings.remove(receivedUUID);
        }
        if (info != null) {
            info.resultListener.onResult(true);
        } else {
            System.out.println("UNKNOWN PONG: " + receivedUUID);
        }
    }
    
    public void sendPing (PingResult callback) {
        PingPongInfo info = new PingPongInfo();
        info.resultListener = callback;
        commFun.sendMessage(CommConstants.TYPE_PING, info.uuid, null);
        synchronized (pendings) {
            pendings.put(info.uuid, info);
        }
    }
    
    public void observe() {
        ArrayList<PingPongInfo> failed = new ArrayList<PingPongInfo>();
        synchronized (pendings) {
            long current = System.currentTimeMillis();
            for (PingPongInfo info : pendings.values()) {
                if (info.timestamp + timelimit > current) {
                    failed.add(info);
                }
            }
            for (PingPongInfo info : failed) {
                pendings.remove(info.uuid);
            }
        }
        for (PingPongInfo info : failed) {
            info.resultListener.onResult(false);
        }
    }
}