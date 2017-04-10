package com.lc.puppet.client.hook.patch.hook.location;

import java.lang.reflect.Method;

import com.lc.puppet.client.hook.base.InterceptorServiceHook;
import com.lc.puppet.service.providers.LocationAMAPHack;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.location.LocationManagerStub;

/**
 * @author Junelegency
 *
 */
public class Interceptor_RequestLocationUpdates extends InterceptorServiceHook {


    @Override
    public String getMethodName() {
        return "requestLocationUpdates";
    }

    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
        return super.call(who, method, args);
    }

    @Override
    public boolean isOnHookConsumed() {
        return true;
    }


    @Override
    public boolean isEnable() {
        return LocationAMAPHack.LOCATION_MOCK_GPS;
    }

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return LocationManagerStub.class;
    }
}
