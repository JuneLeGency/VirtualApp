package com.lc.puppet.client.hook.base;

import java.lang.reflect.Method;

import com.lc.puppet.client.local.interceptor.VInterceptorCallManager;

/**
 * @author Junelegency
 *
 */
public abstract class InterceptorServiceHook extends InterceptorMethod{
    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
        return VInterceptorCallManager.get().call(this,method,args);
    }
}
