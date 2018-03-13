package com.lc.puppet;

import android.os.Parcel;
import android.os.Parcelable;

import com.lc.puppet.client.hook.base.InterceptorMethod;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author legency
 */
public class ICallBody implements Parcelable {

    public String module;

    public String method;

    public Object[] args;

    private Method methodRaw;

    private boolean noReturn;
    private String callerPackage;
    private int callerAppUserId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.module);
        dest.writeString(this.method);
        dest.writeArray(args);
        dest.writeByte((byte)(noReturn ? 1 : 0));
        dest.writeString(signature);
        dest.writeString(callerPackage);
        dest.writeInt(callerAppUserId);
    }

    public static ICallBody create(InterceptorMethod interceptorMethod) {
        return new ICallBody(interceptorMethod);
    }

    public ICallBody(InterceptorMethod interceptorMethod) {
        module = interceptorMethod.getDelegatePatch().getCanonicalName();
        method = interceptorMethod.getMethodName();
    }

    public ICallBody arg(Object... par) {
        this.args = par;
        return this;
    }

    protected ICallBody(Parcel in, ClassLoader classLoader) {
        this.module = in.readString();
        this.method = in.readString();
        this.args = in.readArray(classLoader);
        this.noReturn = in.readByte() != 0;
        this.signature = in.readString();
        this.callerPackage = in.readString();
        this.callerAppUserId = in.readInt();
    }

    public static final Parcelable.ClassLoaderCreator<ICallBody> CREATOR
        = new Parcelable.ClassLoaderCreator<ICallBody>() {
        @Override
        public ICallBody createFromParcel(Parcel source, ClassLoader loader) {
            return new ICallBody(source, loader);
        }

        @Override
        public ICallBody createFromParcel(Parcel in) {
            return new ICallBody(in, null);
        }

        @Override
        public ICallBody[] newArray(int size) {
            return new ICallBody[size];
        }
    };

    @Override
    public String toString() {
        return "ICallBody" + module + "#" + method + "#" + Arrays.toString(args);
    }

    private String signature;

    public String getBookSignature() {
        return signature;
    }

    public boolean hasReturn() {
        return !noReturn;
    }

    public ICallBody method(Method method) {
        this.methodRaw = method;
        generateBookSignature();
        return this;
    }

    private void generateBookSignature() {
        if (methodRaw.getReturnType().equals(Void.TYPE)) {
            this.noReturn = true;
        }
        signature = methodRaw.toGenericString();
    }

    public ICallBody callerPackage(String callerPackage) {
        this.callerPackage = callerPackage;
        return this;
    }

    public ICallBody callerAppUserId(int callerAppUserId) {
        this.callerAppUserId = callerAppUserId;
        return this;
    }
}
