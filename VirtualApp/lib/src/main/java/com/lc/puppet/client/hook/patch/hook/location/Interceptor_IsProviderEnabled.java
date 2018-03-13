package com.lc.puppet.client.hook.patch.hook.location;

import java.lang.reflect.Method;

import com.lc.puppet.client.hook.base.InterceptorMethod;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.location.LocationManagerStub;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;

/**
 * @author Junelegency
 *
 */
public class Interceptor_IsProviderEnabled extends InterceptorMethod {


    @Override
    public String getMethodName() {
        return "isProviderEnabled";
    }

    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
        return true;
    }

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return LocationManagerStub.class;
    }
}
