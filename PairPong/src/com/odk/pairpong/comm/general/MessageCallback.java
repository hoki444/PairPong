package com.odk.pairpong.comm.general;

public interface MessageCallback {
    public void onSuccess();
    public void onError(String reason);
}
