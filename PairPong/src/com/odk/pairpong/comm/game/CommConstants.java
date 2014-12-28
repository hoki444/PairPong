package com.odk.pairpong.comm.game;

public class CommConstants {
    public static String TYPE_START_GAME = "startGame"; // Related to CommOption
    public static String TYPE_SCORE = "score"; // Related to CommScore
    public static String TYPE_RACKET_COLLISION = "racketCollision"; // Related to CommRacketCollision
    public static String TYPE_RACKET_MOVE_COMMAND = "racketMove"; // Related to CommRacketMoveCmd
    public static String TYPE_CEASE_GAME = "ceaseGame"; // Related to (null)
    public static String TYPE_PING = "ping"; // Related to String
    public static String TYPE_PONG = "pong"; // Related to String
    public static String TYPE_END_APP = "endApp"; // Related to (null)
}