package com.lc.puppet.service.providers.base;

import com.lc.puppet.storage.IObFlow;
import com.lc.puppet.storage.IObIndex;
import com.lc.puppet.storage.PaperFlow;

/**
 * Created by legency on 2017/2/1.
 */
public abstract class IOBHookDataProvider<T> extends HookDataProvider<T> {

    private final IObFlow flow;

    public IOBHookDataProvider() {
        this.flow = PaperFlow.get();
    }

    public IOBHookDataProvider(IObFlow flow) {
        this.flow = flow;
    }

    abstract public IObIndex getIObIndex();

    @Override
    public T exec(Object... args) {
        return flow.get(getIObIndex());
    }
}
