package com.lc.puppet.service;

import android.os.RemoteException;
import android.util.Log;

import com.lc.puppet.ICallBody;
import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.IPuppetManager;
import com.lc.puppet.IPuppetStage;
import com.lc.puppet.service.providers.ConnectivityProvider;
import com.lc.puppet.service.providers.LocationManagerProvider;
import com.lc.puppet.service.providers.TelephonyManagerProvider;
import com.lc.puppet.service.providers.WifiManagerProvider;
import com.lc.puppet.service.providers.base.PatchHookProvider;
import com.lc.puppet.storage.IObFlow;
import com.lc.puppet.storage.IObFlowBase;
import com.lc.puppet.storage.PaperFlow;
import com.lc.puppet.storage.backup.PaperExt;
import com.lody.virtual.helper.utils.Reflect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author legency
 */
public class VPuppetsService extends IPuppetManager.Stub {

    public static final String TAG = VPuppetsService.class.getName();
    public static VPuppetsService sService = new VPuppetsService();
    private Map<String, PatchHookProvider> dataProviders = new HashMap<>(12);

    public static VPuppetsService get() {
        return sService;
    }

    IObFlow iobFlow;

    public VPuppetsService() {
        init();
    }

    private HashSet<IPuppetStage> mPuppetStages = new HashSet<>();

    private void init() {
        iobFlow = IObFlowBase.get();
        add(new ConnectivityProvider());
        add(new LocationManagerProvider());
        add(new WifiManagerProvider());
        add(new TelephonyManagerProvider());
    }

    private void add(PatchHookProvider provider) {
        if (dataProviders.containsKey(provider.getDelegatePatch().getCanonicalName())) {
            Log.e(TAG, provider.getDelegatePatch().getName() + " is already added");
        } else {
            dataProviders.put(provider.getDelegatePatch().getCanonicalName(), provider);
        }
    }

    @Override
    public IObjectWrapper call(ICallBody iCall) {
        return dispatchCall(iCall);
    }

    @Override
    public IObjectWrapper getInEnv(String key,String env) {
        return new IObjectWrapper(iobFlow.getInEnv(key,env));
    }

    @Override
    public boolean save(String key, IObjectWrapper iObjectWrapper) {
        return iobFlow.save(key, iObjectWrapper);
    }

    @Override
    public boolean saveMethodResult(ICallBody iCallBody, IObjectWrapper iObjectWrapper) {
        return iobFlow.save(iCallBody, iObjectWrapper);
    }


    private IObjectWrapper dispatchCall(ICallBody call) {
        PatchHookProvider patchHookProvider = dataProviders.get(call.module);
        if (patchHookProvider == null) {
            Log.e(TAG, call.module + " provider not found");
            return null;
        }

        Object object = null;
        try {

            object = Reflect.on(patchHookProvider).callBest(call.method, call.args).get();
            //TODO reconstruct
//            object = patchHookProvider.call(call.method, call.args);
        } catch (Exception e) {
            Log.e(TAG, call + " failed", e);
        }
        if (object == null) {
            object = PaperFlow.get().get(call);
        }
        return new IObjectWrapper(object);
    }

    @Override
    public void setCurrentEnv(String menu) {
        iobFlow.setCurrentEnv(menu);
    }

    @Override
    public void addEnv(String menu) {
        iobFlow.addEnv(menu);
    }

    @Override
    public void delEnv(String menu) {
        iobFlow.delEnv(menu);
    }

    @Override
    public List<String> getEnvs() {
        return iobFlow.getEnvs();
    }

    @Override
    public String getCurrentEnv() {
        return iobFlow.getCurrentEnv();
    }

    @Override
    public void backup() {
        PaperExt.getPaperExt().copy();
    }

    @Override
    public void recover() {
        PaperExt.getPaperExt().recover();
    }

    @Override
    public void bindChange(IPuppetStage iPuppetStage) throws RemoteException {
        mPuppetStages.add(iPuppetStage);
        iPuppetStage.onPuppetChanged(IObFlowBase.get().isObFLowEnable());
    }

    @Override
    public void setObFlowEnable(boolean isEnable) throws RemoteException {
        iobFlow.setObFlowEnable(isEnable);
        notifyChanged(isEnable);
    }

    void notifyChanged(boolean isPuppetEnabled) {
        for (Iterator<IPuppetStage> i = mPuppetStages.iterator(); i.hasNext(); ) {
            IPuppetStage puppetStage = i.next();
            try {
                puppetStage.onPuppetChanged(isPuppetEnabled);
            } catch (RemoteException e) {
                e.printStackTrace();
                i.remove();
            }
        }
    }
}
