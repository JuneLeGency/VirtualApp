package com.lody.virtual.dexdump;

import java.io.File;
import java.io.FileOutputStream;

import android.util.Log;
import com.lody.virtual.client.VClientImpl;
import mirror.android.dex.DexM;
import mirror.java.lang.ClassM;
import mirror.java.lang.DexCacheM;

import static com.lody.virtual.DelegateApplication64Bit.findField;

/**
 * @author legency
 * @date 2018/04/29.
 */
public class DumpUtils {
    public static final String TAG = "DUMP";

    public static void dump(Class clazz, String packageName) {
        try {
            Object o = ClassM.dexCache.get(clazz);
            Object dex = DexCacheM.getDex.call(o);
            byte[] bytes = DexM.getBytes.call(dex);
            String path = "/sdcard/dumptest/" + packageName;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path, "source-" + bytes.length + ".dex");
            writeByteToFile(bytes, file.getAbsolutePath());
            Log.d(TAG, clazz + "fileSaved");
        } catch (Exception e) {
            Log.e(TAG, clazz + "fileFailed", e);
        }
    }

    public static void writeByteToFile(byte[] data, String path) {
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(path);
            localFileOutputStream.write(data);
            localFileOutputStream.close();
            Log.d("asdasd", path + "fileSaved");
        } catch (Exception e) {
            Log.e("asdasd", path + "fileSaved", e);
        }
    }

    public static void dumpByFind(String className, String packageName) {
        ClassLoader c = VClientImpl.get().getClassLoader(VClientImpl.get().getCurrentApplicationInfo());
        Class<?> clazz = null;
        try {
            clazz = c.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dump(clazz,packageName);
    }
}
