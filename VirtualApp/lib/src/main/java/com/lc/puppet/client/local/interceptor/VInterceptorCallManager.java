package com.lc.puppet.client.local.interceptor;

import java.lang.reflect.Method;

import android.os.IBinder;
import android.os.RemoteException;

import com.lc.puppet.ICallBody;
import com.lc.puppet.IPuppetManager;
import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.client.hook.base.InterceptorMethod;
import com.lc.puppet.storage.IObIndex;
import com.lc.puppet.storage.transfers.ClientTransfer;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.os.VUserHandle;

/**
 * @author legency
 */
public class VInterceptorCallManager {

    private static final VInterceptorCallManager sMgr = new VInterceptorCallManager();

    private boolean autoSave;

    public static VInterceptorCallManager get() {
        return sMgr;
    }

    private IPuppetManager mRemote;

    public synchronized IPuppetManager getInterface() {
        if (mRemote == null) {
            synchronized (VInterceptorCallManager.class) {
                if (mRemote == null) {
                    final IBinder pmBinder = ServiceManagerNative.getService(ServiceManagerNative.INTERCEPTOR_SERVICE);
                    mRemote = IPuppetManager.Stub.asInterface(pmBinder);
                }
            }
        }
        return mRemote;
    }

    public Object call(InterceptorMethod interceptorMethod, Method method, Object... objects) {
        try {
            ICallBody callBody = ICallBody.create(interceptorMethod).method(method).arg(objects).callerPackage(
                VClientImpl.get().getCurrentPackage()).callerAppUserId(getAppUserId());
            IObjectWrapper object = getInterface().call(callBody);
            if (!callBody.hasReturn()) {
                return null;
            }
            if (autoSave) {
                //开启此项 采用 调用方法 和参数来自动保存数据结果
                getInterface().saveMethodResult(callBody, object);
            }
            return object != null ? object.get() : null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static int getVUid() {
        return VClientImpl.get().getVUid();
    }

    public static int getAppUserId() {
        return VUserHandle.getUserId(getVUid());
    }

    public Object getInEnv(IObIndex index, String env) {
        try {
            IObjectWrapper object = getInterface().getInEnv(index.name(), env);
            ClientTransfer iObTransfer = index.getClientTransfer();
            return iObTransfer == null ? object.get() : iObTransfer.transferToApiObj(object);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    public void saveTestLocation() {
    //        Location location = new Location("gps");
    //        //测试浙江
    //        location.setLongitude(121.45441);
    //        location.setLatitude(28.451448);
    //
    //        location.setAccuracy(32.0F);
    //        location.setAltitude(0.0);
    //        location.setBearing(0.0F);
    //        Bundle bundle = new Bundle();
    //        bundle.putInt("satellites", 5);
    //        location.setExtras(bundle);
    //        Reflect.on(location).call("setIsFromMockProvider", false);
    //        location.setTime(System.currentTimeMillis());
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    //            location.setElapsedRealtimeNanos(317611316791260L);
    //        }
    //        save(IObIndex.LOCATION, location);
    //    }

    public void save(IObIndex key, Object object) {
        if (object == null) { return; }
        try {
            ClientTransfer iObTransfer = key.getClientTransfer();
            getInterface().save(key.name(),
                iObTransfer == null ? new IObjectWrapper(object) : iObTransfer.transferToProxyObj(object));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void backup() {
        try {
            getInterface().backup();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void recover() {
        try {
            getInterface().recover();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
