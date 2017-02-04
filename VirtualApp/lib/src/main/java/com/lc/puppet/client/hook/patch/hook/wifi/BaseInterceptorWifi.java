package com.lc.puppet.client.hook.patch.hook.wifi;

import com.lc.puppet.client.hook.base.InterceptorServiceHook;
import com.lody.virtual.client.hook.base.PatchDelegate;
import com.lody.virtual.client.hook.patchs.wifi.WifiManagerPatch;

/**
 * @author legency
 */
public abstract class BaseInterceptorWifi extends InterceptorServiceHook {
    @Override
    public Class<? extends PatchDelegate> getDelegatePatch() {
        return WifiManagerPatch.class;
    }
}
