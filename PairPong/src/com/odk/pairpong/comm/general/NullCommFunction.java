package com.odk.pairpong.comm.general;

public class NullCommFunction implements CommFunction {

    @Override
    public void sendMessage(String msgType, Object obj, MessageCallback callback) {
    }

    @Override
    public void startService(String serviceName, MessageCallback callback) {
    }

    @Override
    public void startActivity(String activityName, MessageCallback callback) {
    }

    @Override
    public <T> void registerListener(MessageListener<T> msgLisnr) {
    }

    @Override
    public <T> void unregisterListener(MessageListener<T> msgLisnr) {
    }

    @Override
    public boolean isConnected() {
        return true;
    }

}
