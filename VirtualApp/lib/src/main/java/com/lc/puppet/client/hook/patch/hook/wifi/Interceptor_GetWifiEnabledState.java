package com.lc.puppet.client.hook.patch.hook.wifi;

import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * @author legency
 */
public class Interceptor_GetWifiEnabledState extends BaseInterceptorWifi {

    @Override
    public String getName() {
        return "getWifiEnabledState";
    }

    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
        return WifiManager.WIFI_STATE_ENABLED;
    }


}
