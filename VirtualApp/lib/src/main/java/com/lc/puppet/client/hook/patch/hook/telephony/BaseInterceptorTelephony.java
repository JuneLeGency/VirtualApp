package com.lc.puppet.client.hook.patch.hook.telephony;

import com.lc.puppet.client.hook.base.InterceptorServiceHook;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.telephony.TelephonyStub;

/**
 * @author Junelegency
 *
 */
public abstract class BaseInterceptorTelephony extends InterceptorServiceHook{
    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return TelephonyStub.class;
    }
}
