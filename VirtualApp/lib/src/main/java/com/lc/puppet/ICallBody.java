package com.lc.puppet;

import android.os.Parcel;
import android.os.Parcelable;

import com.lc.puppet.client.hook.base.InterceptorMethod;

import java.util.Arrays;

/**
 * @author legency
 */
public class ICallBody implements Parcelable {

    public String module;

    public String method;

    public Object[] args;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.module);
        dest.writeString(this.method);
        dest.writeArray(args);
    }

    public static ICallBody create(InterceptorMethod interceptorMethod){
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
    }

    public static final Parcelable.ClassLoaderCreator<ICallBody> CREATOR
            = new Parcelable.ClassLoaderCreator<ICallBody>() {
        @Override
        public ICallBody createFromParcel(Parcel source, ClassLoader loader) {
            return new ICallBody(source, loader);
        }

        public ICallBody createFromParcel(Parcel in) {
            return new ICallBody(in, null);
        }


        public ICallBody[] newArray(int size) {
            return new ICallBody[size];
        }
    };

    @Override
    public String toString() {
        return "ICallBody{" +
                "module='" + module + '\'' +
                ", method='" + method + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    private String signature;

    public String getBookSignature() {
        if (signature != null) {
            return signature;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@");
        stringBuilder.append(String.valueOf(module));
        stringBuilder.append("#");
        stringBuilder.append(String.valueOf(method));
        for (Object object : args) {
            stringBuilder.append(object.getClass().getCanonicalName());
            stringBuilder.append("`");
        }
        signature = stringBuilder.toString();
        return signature;
    }
}
