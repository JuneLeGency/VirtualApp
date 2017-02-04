package com.lc.puppet.service.providers.base;

/**
 * Created by legency on 2017/2/1.
 */

public abstract class IOBHookDataWithFake<T> extends IOBHookDataProvider<T> {

    @Override
    public T exec(Object... args) {
        T o = super.exec(args);
        return o == null ? createFakeData() : o;
    }

    abstract public T createFakeData();
}
