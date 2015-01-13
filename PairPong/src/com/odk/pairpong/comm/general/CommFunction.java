package com.odk.pairpong.comm.general;



public interface CommFunction {
    
    public abstract void sendMessage(String msgType, Object obj, MessageCallback callback);
	public abstract void startService(String serviceName, MessageCallback callback);
	public abstract void startActivity(String activityName, MessageCallback callback);

    public abstract <T> void registerListener (MessageListener<T> msgLisnr);
    public abstract <T> void unregisterListener (MessageListener<T> msgLisnr);
    
    public boolean isConnected();
}