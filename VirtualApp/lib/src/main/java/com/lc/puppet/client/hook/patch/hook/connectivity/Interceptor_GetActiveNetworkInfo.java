package com.lc.puppet.client.hook.patch.hook.connectivity;

import com.lc.puppet.client.hook.base.InterceptorServiceHook;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.connectivity.ConnectivityStub;

/**
 * @author Junelegency
 *
 */
public class Interceptor_GetActiveNetworkInfo extends InterceptorServiceHook {

    @Override
    public String getMethodName() {
        return "getActiveNetworkInfo";
    }

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return ConnectivityStub.class;
    }
}
