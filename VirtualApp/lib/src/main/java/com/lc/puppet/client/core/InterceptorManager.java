package com.lc.puppet.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;
import com.lc.puppet.client.hook.base.InterceptorMethod;
import com.lc.puppet.client.hook.patch.hook.connectivity.Interceptor_GetActiveNetworkInfo;
import com.lc.puppet.client.hook.patch.hook.location.Interceptor_RemoveUpdates;
import com.lc.puppet.client.hook.patch.hook.location.Interceptor_RequestLocationUpdates;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetActivePhoneTypeForSlot;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetActivePhoneTypeForSubscriber;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetAllCellInfo;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetAllCellInfoUsingSubId;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetCellLocation;
import com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetNeighboringCellInfo;
import com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetConnectionInfo;
import com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetScanResults;
import com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetWifiEnabledState;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.interfaces.IInjector;

/**
 * @author Junelegency
 */
public class InterceptorManager {

    static List<InterceptorMethod> interceptorMethods = new ArrayList<>();

    static InterceptorManager interceptorManager = new InterceptorManager();

    public static final String TAG = "InterceptorManager";

    static {
        // connectivity interceptor
        interceptorMethods.add(new Interceptor_GetActiveNetworkInfo());
        //location interceptor
        interceptorMethods.add(new Interceptor_RemoveUpdates());
        interceptorMethods.add(new Interceptor_RequestLocationUpdates());

        // telephony interceptor
        interceptorMethods.add(new Interceptor_GetActivePhoneTypeForSlot());
        interceptorMethods.add(new Interceptor_GetActivePhoneTypeForSubscriber());
        interceptorMethods.add(new Interceptor_GetAllCellInfo());
        interceptorMethods.add(new Interceptor_GetAllCellInfoUsingSubId());
        interceptorMethods.add(new Interceptor_GetCellLocation());
        interceptorMethods.add(new Interceptor_GetNeighboringCellInfo());

        // wifi interceptor
        interceptorMethods.add(new Interceptor_GetConnectionInfo());
        interceptorMethods.add(new Interceptor_GetScanResults());
        interceptorMethods.add(new Interceptor_GetWifiEnabledState());
    }

    enum InterceptorType {
        REPLACE,
        ADD,
        SET
    }

    class Replaced {
        InterceptorMethod hook;
        InterceptorType type;
        MethodProxy target;

        public Replaced(InterceptorMethod hook, InterceptorType type, MethodProxy target) {
            this.hook = hook;
            this.type = type;
            this.target = target;
        }
    }

    List<Replaced> replacedList = new ArrayList<>();

    public static InterceptorManager get() {
        return interceptorManager;
    }

    private boolean hasApplied = false;

    public static List<InterceptorMethod> getInterceptors() {
        return interceptorMethods;
    }

    /**
     * TODO 过滤 自定义配置 进程 包名  指定场景下的 拦截器
     *
     * @return
     */
    public List<InterceptorMethod> getInterceptorsByFilter() {
        return null;
    }

    synchronized public void applyInterceptors(Map<Class<?>, IInjector> injectableMap) {
        if (hasApplied) { return; }
        List<InterceptorMethod> interceptors = getInterceptors();
        if (interceptors == null || interceptors.isEmpty()) { return; }
        for (InterceptorMethod interceptorMethod : interceptors) {
            try {
                IInjector injectable = injectableMap.get(interceptorMethod.getDelegatePatch());
                if (injectable instanceof MethodInvocationProxy) {
                    Object iHookObject = ((MethodInvocationProxy)injectable).getInvocationStub();
                    if (iHookObject instanceof MethodInvocationStub) {
                        MethodProxy targetMethodProxy = ((MethodInvocationStub)iHookObject)
                            .getMethodProxy(interceptorMethod.getMethodName());

                        boolean setHook = false;
                        InterceptorType injectType = InterceptorType.ADD;
                        if (targetMethodProxy != null) {
                            if (interceptorMethod.replaceOriginal()) {
                                ((MethodInvocationStub)iHookObject).removeMethodProxy(targetMethodProxy);
                                injectType = InterceptorType.REPLACE;
                            } else {
                                setHook = true;
                                injectType = InterceptorType.SET;
                            }
                        }
                        if (setHook) {
                            //targetMethodProxy.setInterceptHook(interceptorMethod);
                        } else {
                            ((MethodInvocationStub)iHookObject).addMethodProxy(interceptorMethod);
                        }
                        Log.d(TAG, interceptorMethod + " is " + injectType.toString());
                        replacedList.add(new Replaced(interceptorMethod, injectType, targetMethodProxy));
                    }
                } else {
                    Log.e(TAG, interceptorMethod + " no HookDelegate found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hasApplied = true;
    }

    synchronized public void removeInterceptors(Map<Class<?>, IInjector> injectableMap) {
        if (!hasApplied || replacedList == null || replacedList.size() == 0) { return; }
        for (Replaced re : replacedList) {
            InterceptorMethod interceptorMethod = re.hook;
            IInjector injectable = injectableMap.get(interceptorMethod.getDelegatePatch());
            if (injectable instanceof MethodInvocationProxy) {
                Object iHookObject = ((MethodInvocationProxy)injectable).getInvocationStub();
                if (iHookObject instanceof MethodInvocationStub) {
                    MethodInvocationStub methodInvocationStub = (MethodInvocationStub)iHookObject;
                    MethodProxy target = re.target;
                    switch (re.type) {
                        case REPLACE:
                            methodInvocationStub.removeMethodProxy(interceptorMethod);
                            methodInvocationStub.addMethodProxy(target);
                            break;
                        case ADD:
                            methodInvocationStub.removeMethodProxy(interceptorMethod);
                            break;
                        case SET:
                            //target.removeInterceptorHook();
                            break;
                    }
                }
            }
        }
        hasApplied = false;
    }
}
