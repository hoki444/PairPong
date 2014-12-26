package com.odk.pairpong.comm.general;

public interface MessageListener <T> {
    String getTypeName ();
    Class<T> getTypeClass();
    void onReceive(T obj);
}