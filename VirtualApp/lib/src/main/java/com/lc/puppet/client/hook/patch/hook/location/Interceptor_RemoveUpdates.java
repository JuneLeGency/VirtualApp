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
public class Interceptor_RemoveUpdates extends InterceptorMethod {

    @Override
    public boolean beforeCall(Object who, Method method, Object... args) {
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.beforeCall(who, method, args);
    }

    @Override
    public String getMethodName() {
        return "removeUpdates";
    }

    @Override
    public boolean isOnHookConsumed() {
        return true;
    }

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return LocationManagerStub.class;
    }
}
