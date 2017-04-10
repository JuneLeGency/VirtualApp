package com.lc.puppet.client.hook.patch.hook.wifi;

import java.lang.reflect.Method;

import android.net.wifi.WifiManager;

/**
 * @author legency
 */
public class Interceptor_GetWifiEnabledState extends BaseInterceptorWifi {

    @Override
    public String getMethodName() {
        return "getWifiEnabledState";
    }

    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
        return WifiManager.WIFI_STATE_ENABLED;
    }


}
