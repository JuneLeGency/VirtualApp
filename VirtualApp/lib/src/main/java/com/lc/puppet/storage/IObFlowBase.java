package com.lc.puppet.storage;

import android.content.Context;
import android.util.Log;

import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.storage.transfers.ServerTransfer;
import com.lody.virtual.helper.utils.Reflect;

import java.util.List;

/**
 * @author legency
 */
public abstract class IObFlowBase implements IObFlow {

    public static final String DEFAULT_MENU = "默认";

    String currentBook = DEFAULT_MENU;

    public List<String> menus;

    public boolean enabled;

    protected Object extractDataBeforeSave(String key, IObjectWrapper iObjectWrapper) throws Throwable {
        ServerTransfer iObTransfer = IObIndex.valueOf(key).getServerTransfer() ;
        Object object = iObTransfer.transferBeforeSave(iObjectWrapper);
        IObType t = IObIndex.valueOf(key).asFiled().getAnnotation(IObType.class);
        Class<?> clazz = t.value();
        if (clazz.isInstance(object) || Reflect.wrapper(clazz).isAssignableFrom(Reflect.wrapper(object.getClass()))) {

        } else {
            Log.w("IOB", String.format("saving %1s type not same transferToProxyObj to %2s target %3s by %4s", key, object.getClass().getSimpleName(), clazz.getSimpleName(), iObTransfer.getClass().getSimpleName()));
        }
        return object;
    }

    protected Object transferDataAfterRead(String key, Object o) throws Throwable {
        if (o == null) {
            throw new Exception(String.format("get data of key : %s failed target is null", key));
        }
        ServerTransfer iObTransfer = IObIndex.valueOf(key).getServerTransfer() ;
        Object object = iObTransfer.transferAfterRead(o);
        IObType t = IObIndex.valueOf(key).asFiled().getAnnotation(IObType.class);
        Class<?> clazz = t.value();
        if (clazz.isInstance(object) || Reflect.wrapper(clazz).isAssignableFrom(Reflect.wrapper(object.getClass()))) {
            return object;
        } else {
            throw new Exception(String.format("key %s saved type is not same saved: [%s] target:[%s]", key, object.getClass().getName(), clazz.getName()));
        }
    }

    public static IObFlowBase create(Context context) {
        iObFlowBase = new PaperFlow();
        iObFlowBase.init(context.getApplicationContext());
        return iObFlowBase;
    }

    private static IObFlowBase iObFlowBase;

    public static IObFlowBase get() {
        return iObFlowBase;
    }

    protected abstract void init(Context context);

    public List<String> getEnvs() {
        return menus;
    }

    public void addEnv(String menu) {
        menus.add(menu);
        notifyMenuChanges();
    }

    public void delEnv(String menu) {
        if (menus.remove(menu)) {
            notifyMenuChanges();
        }
    }

    @Override
    public String getCurrentEnv() {
        return currentBook;
    }

    @Override
    public void setCurrentEnv(String currentBook) {
        if (menus.contains(currentBook)) {
            this.currentBook = currentBook;
        }
    }

    protected abstract void notifyMenuChanges();

    @Override
    public <T> T get(IObIndex key) {
        return get(key.name());
    }

    @Override
    public <T> T get(String key) {
        return getInEnv(key,currentBook);
    }

    @Override
    public boolean save(IObIndex key, IObjectWrapper iObjectWrapper) {
        return save(key.name(), iObjectWrapper);
    }
}
