package com.lc.puppet.client.hook.patch.hook.location;

import com.lc.puppet.client.hook.base.InterceptorMethod;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.location.LocationManagerStub;

/**
 * @author legency
 * @date 2018/03/12.
 */

public class Interceptor_RegisterGnssStatusCallback extends InterceptorMethod{

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return LocationManagerStub.class;
    }

    @Override
    public String getMethodName() {
        return "registerGnssStatusCallback";
    }
}