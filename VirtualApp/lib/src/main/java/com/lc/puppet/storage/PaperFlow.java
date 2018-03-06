package com.lc.puppet.storage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lc.puppet.ICallBody;
import com.lc.puppet.IObjectWrapper;
import com.lc.puppet.storage.backup.PaperExt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

/**
 * @author legency
 */
public class PaperFlow extends IObFlowBase {

    public static final String MENU_KEY = "MENU";

    public static final String MENU_CURRENT = "MENU_CURRENT";

    public static final String PUPPET_ENABLE = "PUPPET_ENABLE";

    public PaperFlow() {
        Paper.addSerializer(Bundle.class, new Serializer<Bundle>() {
            @Override
            public void write(Kryo kryo, Output output, Bundle object) {
                mirror.android.os.Bundle.BaseBundle.unparcel.call(object);
                Map<String, Object> map = mirror.android.os.Bundle.BaseBundle.mMap.get(object);
                kryo.writeClassAndObject(output, map);
            }

            @Override
            public Bundle read(Kryo kryo, Input input, Class<Bundle> type) {
                Bundle bundle1 = new Bundle();
                Object o = kryo.readClassAndObject(input);
                mirror.android.os.Bundle.BaseBundle.mMap.set(bundle1, (Map<String, Object>) o);
                return bundle1;
            }
        });
    }

    @Override
    public String getCurrentEnv() {
        return currentBook;
    }

    @Override
    public void setCurrentEnv(String currentBook) {
        super.setCurrentEnv(currentBook);
        Paper.book().write(MENU_CURRENT, currentBook);
    }

    @Override
    public boolean save(String key, IObjectWrapper iObjectWrapper) {
        try {
            Paper.book(currentBook).write(key, extractDataBeforeSave(key, iObjectWrapper));
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public <T> T getInEnv(String key,String env) {
        try {
            if(Paper.book(env).exist(key)) {
                return (T)transferDataAfterRead(key, Paper.book(env).read(key));
            }else{
                Log.e("paperFlow",key+ " not exist in "+env);
                return null;
                //throw new KeyNotExistException(key +" in paper not exist");
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public <T> T get(ICallBody key) {
        return Paper.book(currentBook).read(key.getBookSignature());
    }

    @Override
    public boolean save(ICallBody key, IObjectWrapper iObjectWrapper) {
        try {
            Paper.book(currentBook).write(key.getBookSignature(), iObjectWrapper.get());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean isObFLowEnable() {
        return enabled;
    }

    @Override
    public boolean setObFlowEnable(boolean enabled) {
        if (enabled == this.enabled) return enabled;
        this.enabled = enabled;
        Paper.book().write(PUPPET_ENABLE, enabled);
        return enabled;
    }

    @Override
    public void init(Context context) {
        Paper.init(context);
        PaperExt.getPaperExt(context);
        List<String> menus = Paper.book().read(MENU_KEY);
        if (menus != null && menus.size() > 0) {
            this.menus = menus;
        } else {
            menus = new ArrayList<>();
            menus.add(DEFAULT_MENU);
            this.menus = menus;
            Paper.book().write(MENU_KEY, menus);
        }

        String current = Paper.book().read(MENU_CURRENT);
        if (TextUtils.isEmpty(current)) {
            Paper.book().write(MENU_CURRENT, currentBook);
        } else {
            currentBook = current;
        }
        enabled = Paper.book().read(PUPPET_ENABLE, false);
    }

    @Override
    protected void notifyMenuChanges() {
        Paper.book().write(MENU_KEY, menus);
    }

    @Override
    public void addEnv(String menu) {
        super.addEnv(menu);
    }

    @Override
    public void delEnv(String menu) {
        super.delEnv(menu);
        Paper.book(menu).destroy();
    }
}
