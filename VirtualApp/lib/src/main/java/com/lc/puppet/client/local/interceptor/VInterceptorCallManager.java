package com.lc.puppet.client.local.interceptor;

import android.os.IBinder;
import android.os.RemoteException;

import com.lc.puppet.ICallBody;
import com.lc.puppet.IPuppetManager;
import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.client.hook.base.InterceptorHook;
import com.lc.puppet.storage.IObIndex;
import com.lc.puppet.storage.IObType;
import com.lc.puppet.storage.transfers.ClientTransfer;
import com.lody.virtual.client.ipc.ServiceManagerNative;

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

    public Object call(InterceptorHook interceptorHook, Object... objects) {
        try {

            ICallBody callBody = ICallBody.create(interceptorHook).arg(objects);
            IObjectWrapper object = getInterface().call(callBody);
            if (autoSave) {
                getInterface().saveMethodResult(callBody, object);
            }
            return object != null ? object.get() : null;
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
        save(key.name(), object);
    }

    public void save(String key, Object object) {
        if (object == null)
            return;
        try {
            IObType t = IObIndex.valueOf(key).asFiled().getAnnotation(IObType.class);
            Class<?> tr = t.clientTransfer();
            ClientTransfer iObTransfer;
            IObjectWrapper objectWrapper;
            //排除默认
            if (!tr.isInterface() && tr != Void.class && ClientTransfer.class.isAssignableFrom(tr)) {
                iObTransfer = (ClientTransfer) tr.newInstance();
                objectWrapper = iObTransfer.transferToProxyObj(object);
                getInterface().save(key, objectWrapper);
            } else {
                getInterface().save(key, new IObjectWrapper(object));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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
