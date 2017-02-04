package com.lc.puppet;
import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.ICallBody;
import com.lc.puppet.IPuppetStage;

interface IPuppetManager {

    IObjectWrapper call(in ICallBody iCallBody);

    boolean save(String key, in IObjectWrapper iObjectWrapper);

    //针对同步返回结果的方法 可以直接保存数据
    boolean saveMethodResult(in ICallBody iCallBody, in IObjectWrapper iObjectWrapper);

    List<String> getEnvs();

    void setCurrentEnv(String menu);

    String getCurrentEnv();

    void addEnv(String menu);

    void delEnv(String menu);

    void backup();

    void recover();

    void bindChange(IPuppetStage i);

    void setObFlowEnable(boolean isEnable);
}