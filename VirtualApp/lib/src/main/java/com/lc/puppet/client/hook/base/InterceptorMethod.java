package com.lc.puppet.client.hook.base;

import java.lang.reflect.Method;

import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;

/**
 * @author Junelegency
 *
 */
public abstract class InterceptorMethod extends MethodProxy {

    protected  boolean isOnHookConsumed(){
        return false;
    }

    public boolean isOnHookEnabled(){
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public Object call(Object who, Method method, Object... args) throws Throwable {
//        Log.d("InterceptorMethod",this+ "called");
        return super.call(who, method, args);
    }

    abstract public Class<? extends MethodInvocationProxy> getDelegatePatch();

    public boolean replaceOriginal() {
        return true;
    }

    @Override
    public String toString() {
        return "InterceptorMethod{" + getDelegatePatch().getSimpleName() + " " + " method " + getMethodName() + " }";
    }
}
