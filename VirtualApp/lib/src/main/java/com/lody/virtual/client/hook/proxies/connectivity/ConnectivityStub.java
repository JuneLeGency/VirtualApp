package com.lody.virtual.client.hook.proxies.connectivity;

import java.lang.reflect.Method;

import android.content.Context;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.stub.StubVpnService;
import mirror.android.net.IConnectivityManager;
import mirror.com.android.internal.net.VpnConfig;

/**
 * @author legency
 */
public class ConnectivityStub extends BinderInvocationProxy {

    public ConnectivityStub() {
        super(IConnectivityManager.Stub.asInterface, Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new ReplaceCallingPkgMethodProxy("prepareVpn"));
        addMethodProxy(new establishVpn());
    }
    private static class establishVpn extends MethodProxy {
        @Override
        public String getMethodName() {
            return "establishVpn";
        }

        @Override
        public boolean beforeCall(Object who, Method method, Object... args) {
            VpnConfig.user.set(args[0], StubVpnService.class.getName());
            return super.beforeCall(who, method, args);
        }

    }
}
