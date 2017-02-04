package com.lc.puppet.service.providers.base;

/**
 * Created by legency on 2017/2/1.
 */
public abstract class HookDataProvider<T> {
    abstract public String getName();
    abstract public T exec(Object... args);

}
