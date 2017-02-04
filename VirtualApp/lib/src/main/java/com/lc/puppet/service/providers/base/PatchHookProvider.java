package com.lc.puppet.service.providers.base;

import com.lc.puppet.storage.IObFlow;
import com.lc.puppet.storage.IObIndex;
import com.lc.puppet.storage.PaperFlow;
import com.lody.virtual.client.hook.base.PatchDelegate;

import java.util.HashMap;

/**
 * @author legency
 */
public abstract class PatchHookProvider {

    HashMap<String, HookDataProvider> hookDataProviders = new HashMap<>();

    public PatchHookProvider() {
        addHookDataProviders();
    }

    protected void addHookDataProviders() {
    }

    protected void addProvider(HookDataProvider hookDataProvider) {
        hookDataProviders.put(hookDataProvider.getName(), hookDataProvider);
    }

    abstract public Class<? extends PatchDelegate> getDelegatePatch();

    protected <T> T callDataWithCreator(String key, IDataCreator<T> paperDataCreator) {
        return paperDataCreator.exec(key);
    }

    protected <T> T callDataWithCreator(IObIndex key, IDataCreator<T> paperDataCreator) {
        return callDataWithCreator(key.name(), paperDataCreator);
    }

    public Object call(String method, Object[] args) {
        HookDataProvider provider = hookDataProviders.get(method);
        if (provider != null) {
            return provider.exec(args);
        }
        return null;
    }

    public interface IDataCreator<T> {

        IObFlow get();

        T createFakeData();

        T exec(String key);
    }

    public abstract class PaperDataCreator<T> implements IDataCreator<T> {

        @Override
        public IObFlow get() {
            return PaperFlow.get();
        }


        public T exec(String key) {
            T o = get().get(key);
            return o == null ? createFakeData() : o;
        }
    }

}
