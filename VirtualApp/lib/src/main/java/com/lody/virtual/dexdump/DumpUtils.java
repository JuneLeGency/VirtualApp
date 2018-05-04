package com.lody.virtual.dexdump;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.FileUtils;
import com.taobao.android.dexposed.ClassUtils;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;
import mirror.android.dex.DexM;
import mirror.java.lang.ClassM;
import mirror.java.lang.DexCacheM;

/**
 * @author legency
 * @date 2018/04/29.
 */
public class DumpUtils {

    public static final String TAG = "DUMP";

    public static final String FOLDER = "dexDump";

    private static ApplicationInfo sAppInfo = null;

    public static ApplicationInfo getCurrAppInfo() {
        if (sAppInfo == null) { sAppInfo = VClientImpl.get().getCurrentApplicationInfo(); }
        return sAppInfo;
    }

    public static String getProcessName() {
        ApplicationInfo appInfo = getCurrAppInfo();
        //返回修正后的
        if (appInfo != null) {
            return appInfo.processName;
        }
        //返回修正前的
        return VirtualCore.get().getProcessName();
    }

    public static String getPackage() {
        ApplicationInfo appInfo = getCurrAppInfo();
        //返回修正后的
        if (appInfo != null) {
            return appInfo.packageName;
        }
        return VClientImpl.get().getCurrentPackage();
    }

    public static void dump(Class clazz) {
        try {
            Object o = ClassM.dexCache.get(clazz);
            Object dex = DexCacheM.getDex.call(o);
            String location = DexCacheM.location.get(o);
            if (!checkPass(location)) {
                return;
            }
            byte[] bytes = DexM.getBytes.call(dex);
            String path = Environment.getExternalStorageDirectory() + File.separator + FOLDER + File.separator
                + getProcessName();
            File dir = new File(path);
            if (!dir.exists()) {
                boolean b = dir.mkdirs();
                if (!b) {
                    return;
                }
            }
            File file = new File(path, "s_" + bytes.length + ".dex");
            if (file.exists()) {
                return;
            }
            File config = new File(path, "s_" + bytes.length + ".ini");
            FileUtils.writeToFile(location.getBytes(), config);
            writeByteToFile(bytes, file.getAbsolutePath());
            Log.d(TAG, file.getName() + "fileSaved");
        } catch (Exception e) {
            Log.e(TAG, clazz + "fileFailed", e);
        }
    }

    //360 加固
    public static final String[] whiteList = {".jiagu", "/system/app/"};

    public static final String[] blackList = {"miuisystem.apk", "miui.apk", "core-libart.jar",
        "system/framework/framework.jar"};

    private static boolean checkPass(String location) {
        if (location.contains(getPackage())) {
            return true;
        }
        for (String s : whiteList) {
            if (location.contains(s)) {
                return true;
            }
        }
        for (String s : blackList) {
            if (location.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static void writeByteToFile(byte[] data, String path) {
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(path);
            localFileOutputStream.write(data);
            localFileOutputStream.close();
            Log.d(TAG, path + "fileSaved");
        } catch (Exception e) {
            Log.e(TAG, path + "fileSaved", e);
        }
    }

    public static void dumpByFind(String className) {
        ClassLoader c = getClassLoader();
        Class<?> clazz = null;
        try {
            clazz = c.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dump(clazz);
    }

    public static ClassLoader getClassLoader() {
        return VClientImpl.get().getClassLoader(VClientImpl.get().getCurrentApplicationInfo());
    }

    public static void hook(ClassLoader classLoader) {
        try {
            DexposedBridge.findAndHookMethod(ClassUtils.getClass(classLoader, "java.lang.ClassLoader", false),
                "loadClass",
                String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("hook", "classLoader loadClass" + param.args[0]);
                        Class result = (Class)param.getResult();
                        if (result != null) {
                            DumpUtils.dump(result);
                        }
                    }
                });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //DexposedBridge.findAndHookMethod(ClassLoader.class, "loadClass", String.class, boolean.class,
        //    new XC_MethodHook() {
        //        @Override
        //        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //            Log.d("hook", "loadClass"+param.args[0]);
        //            Class result = (Class)param.getResult();
        //            if (result != null) {
        //                DumpUtils.dump(result);
        //            }
        //        }
        //    });
        //HookManager.getDefault().applyHooks(ClassLoaderM.class);
    }

    public static void dump(Application app) {
        dump(app.getClass());
        hook(app.getClassLoader());
    }
}
