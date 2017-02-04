package com.lc.puppet.client.core;

import android.util.Log;

import com.lc.puppet.client.hook.base.InterceptorHook;
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
import com.lody.virtual.client.hook.base.Hook;
import com.lody.virtual.client.hook.base.HookDelegate;
import com.lody.virtual.client.hook.base.PatchDelegate;
import com.lody.virtual.client.interfaces.Injectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Junelegency
 */
public class InterceptorManager {

    static List<InterceptorHook> interceptorHooks = new ArrayList<>();

    static InterceptorManager interceptorManager = new InterceptorManager();

    public static final String TAG = "InterceptorManager";

    static {
        // connectivity interceptor
        interceptorHooks.add(new Interceptor_GetActiveNetworkInfo());
        //location interceptor
        interceptorHooks.add(new Interceptor_RemoveUpdates());
        interceptorHooks.add(new Interceptor_RequestLocationUpdates());

        // telephony interceptor
        interceptorHooks.add(new Interceptor_GetActivePhoneTypeForSlot());
        interceptorHooks.add(new Interceptor_GetActivePhoneTypeForSubscriber());
        interceptorHooks.add(new Interceptor_GetAllCellInfo());
        interceptorHooks.add(new Interceptor_GetAllCellInfoUsingSubId());
        interceptorHooks.add(new Interceptor_GetCellLocation());
        interceptorHooks.add(new Interceptor_GetNeighboringCellInfo());

        // wifi interceptor
        interceptorHooks.add(new Interceptor_GetConnectionInfo());
        interceptorHooks.add(new Interceptor_GetScanResults());
        interceptorHooks.add(new Interceptor_GetWifiEnabledState());
    }

    enum InterceptorType {
        REPLACE,
        ADD,
        SET
    }

    class Replaced {
        InterceptorHook hook;
        InterceptorType type;
        Hook target;

        public Replaced(InterceptorHook hook, InterceptorType type, Hook target) {
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

    public static List<InterceptorHook> getInterceptors() {
        return interceptorHooks;
    }

    /**
     * TODO 过滤 自定义配置 进程 包名  指定场景下的 拦截器
     *
     * @return
     */
    public List<InterceptorHook> getInterceptorsByFilter() {
        return null;
    }

    synchronized public void applyInterceptors(Map<Class<?>, Injectable> injectableMap) {
        if (hasApplied)
            return;
        List<InterceptorHook> interceptors = getInterceptors();
        if (interceptors == null || interceptors.isEmpty()) return;
        for (InterceptorHook interceptorHook : interceptors) {
            try {
                Injectable injectable = injectableMap.get(interceptorHook.getDelegatePatch());
                if (injectable instanceof PatchDelegate) {
                    HookDelegate iHookObject = ((PatchDelegate) injectable).getHookDelegate();
                    Hook targetHook = iHookObject.getHook(interceptorHook.getName());
                    boolean setHook = false;
                    InterceptorType injectType = InterceptorType.ADD;
                    if (targetHook != null) {
                        if (interceptorHook.replaceOriginal()) {
                            iHookObject.removeHook(targetHook);
                            injectType = InterceptorType.REPLACE;
                        } else {
                            setHook = true;
                            injectType = InterceptorType.SET;
                        }
                    }
                    if (setHook) {
                        targetHook.setInterceptHook(interceptorHook);
                    } else {
                        iHookObject.addHook(interceptorHook);
                    }
                    Log.d(TAG, interceptorHook + " is " + injectType.toString());
                    replacedList.add(new Replaced(interceptorHook, injectType, targetHook));
                } else {
                    Log.e(TAG, interceptorHook + " no HookDelegate found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hasApplied = true;
    }

    synchronized public void removeInterceptors(Map<Class<?>, Injectable> injectableMap) {
        if (!hasApplied || replacedList == null || replacedList.size() == 0)
            return;
        for (Replaced re : replacedList) {
            InterceptorHook interceptorHook = re.hook;
            Injectable injectable = injectableMap.get(interceptorHook.getDelegatePatch());
            if (injectable instanceof PatchDelegate) {
                HookDelegate iHookObject = ((PatchDelegate) injectable).getHookDelegate();
                Hook target = re.target;
                switch (re.type) {
                    case REPLACE:
                        iHookObject.removeHook(interceptorHook);
                        iHookObject.addHook(target);
                        break;
                    case ADD:
                        iHookObject.removeHook(interceptorHook);
                        break;
                    case SET:
                        target.removeInterceptorHook();
                        break;
                }
            }
        }
        hasApplied = false;
    }
}
