package com.lc.puppet.storage;

import com.lc.puppet.ICallBody;
import com.lc.puppet.IObjectWrapper;

import java.util.List;

/**
 * Created by legency on 2016/9/11.
 */
public interface IObFlow {

    List<String> getEnvs();

    String getCurrentEnv();

    void setCurrentEnv(String currentBook);

    void addEnv(String menu);

    void delEnv(String menu);

    boolean save(String key, IObjectWrapper iObjectWrapper);

    boolean save(IObIndex key, IObjectWrapper iObjectWrapper);

    <T> T get(String key);

    <T> T getInEnv(String key,String env);

    <T> T get(IObIndex key);

    <T> T get(ICallBody key);

    boolean save(ICallBody key, IObjectWrapper iObjectWrapper);

    boolean isObFLowEnable();

    boolean setObFlowEnable(boolean enabled);
}
