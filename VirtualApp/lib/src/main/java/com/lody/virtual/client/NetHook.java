package com.lody.virtual.client;

/**
 * @author legency
 * @date 2018/05/26.
 */
public class NetHook {
    public static void init() {
        try {
            System.loadLibrary("hookdebug");
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}