package com.minhui.vpn;
/**
 * Created by minhui.zhu on 2017/6/24.
 * Copyright © 2017年 minhui.zhu. All rights reserved.
 */

import java.io.FileDescriptor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.util.Log;

public class LocalVPNService extends VpnService implements IVpnService {
    public static final String ACTION_START_VPN = "com.minhui.START_VPN";
    public static final String ACTION_CLOSE_VPN = "com.minhui.roav.CLOSE_VPN";
    private static final String FACEBOOK_APP = "com.facebook.katana";
    private static final String YOUTUBE_APP = "com.google.android.youtube";
    private static final String GOOGLE_MAP_APP = "com.google.android.apps.maps";

    private static final String TAG = LocalVPNService.class.getSimpleName();
    private static final String VPN_ADDRESS = "10.0.0.2"; // Only IPv4 support for now
    private static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything
    private static final String GOOGLE_DNS_FIRST = "8.8.8.8";
    private static final String GOOGLE_DNS_SECOND = "8.8.4.4";
    private static final String AMERICA = "208.67.222.222";
    private static final String HK_DNS_SECOND = "205.252.144.228";
    private static final String CHINA_DNS_FIRST = "114.114.114.114";
    public static final String BROADCAST_VPN_STATE = "com.minhui.localvpn.VPN_STATE";
    public static final String SELECT_PACKAGE_ID = "select_protect_package_id";
    private String selectPackage;

    private VpnController controller;

    @Override
    public void onCreate() {
        super.onCreate();
        controller = new VpnController(this, this);
    }

    private void addAllowedApp(Builder builder, String appName) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                builder.addAllowedApplication(appName);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "vpn failed to allow application,app is " + appName + "error is" + e.getMessage());
            }
        }
    }

    public String getSelectPackage() {
        return selectPackage;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if (ACTION_START_VPN.equals(action)) {
            selectPackage = intent.getStringExtra(SELECT_PACKAGE_ID);
            controller.startLocalVPN();
        } else {
            controller.cleanup();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stopped");
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
        controller.cleanup();
    }

    @Override
    public FileDescriptor getInterceptFd() {
        Builder builder = new Builder();
        builder.addAddress(VPN_ADDRESS, 32);
        builder.addRoute(VPN_ROUTE, 0);
        //某些国外的手机例如google pixel 默认的dns解析器地址不是8.8.8.8 ，不设置会出错
        builder.addDnsServer(GOOGLE_DNS_FIRST);
        builder.addDnsServer(CHINA_DNS_FIRST);
        builder.addDnsServer(GOOGLE_DNS_SECOND);
        builder.addDnsServer(AMERICA);
        builder.setMtu(1280);
        try {
            if (selectPackage != null) {
                builder.addAllowedApplication(selectPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*   addAllowedApp(builder, YOUTUBE_APP);*/
        return builder.setSession(VPNConnectManager.getInstance().getAppName()).establish().getFileDescriptor();
    }
}