package com.lody.virtual.client;

/**
 * @author legency
 * @date 2018/05/26.
 */
public class NetHook {
    public static void init() {
        System.loadLibrary("hookdebug");
    }
}