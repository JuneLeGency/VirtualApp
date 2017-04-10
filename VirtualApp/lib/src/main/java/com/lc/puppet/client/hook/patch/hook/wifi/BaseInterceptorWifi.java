package com.lc.puppet.client.hook.patch.hook.wifi;

import com.lc.puppet.client.hook.base.InterceptorServiceHook;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.wifi.WifiManagerStub;

/**
 * @author legency
 */
public abstract class BaseInterceptorWifi extends InterceptorServiceHook {
    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return WifiManagerStub.class;
    }
}
