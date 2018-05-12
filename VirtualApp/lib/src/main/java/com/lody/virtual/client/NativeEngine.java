package com.lody.virtual.client;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Binder;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.natives.NativeMethods;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VUserHandle;
import com.lody.virtual.remote.InstalledAppInfo;
import com.minhui.vpn.IVpnService;
import com.minhui.vpn.VpnController;

/**
 * VirtualApp Native Project
 */
public class NativeEngine {

    private static final String TAG = NativeEngine.class.getSimpleName();

    private static Map<String, InstalledAppInfo> sDexOverrideMap;

    private static boolean sFlag = false;
    private static Socket socket;
    private static VpnController mController;

    static {
        try {
            System.loadLibrary("va++");
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    static {
        NativeMethods.init();
    }

    public static void startDexOverride() {
        List<InstalledAppInfo> installedAppInfos = VirtualCore.get().getInstalledApps(0);
        sDexOverrideMap = new HashMap<>(installedAppInfos.size());
        for (InstalledAppInfo info : installedAppInfos) {
            try {
                sDexOverrideMap.put(new File(info.apkPath).getCanonicalPath(), info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRedirectedPath(String origPath) {
        try {
            return nativeGetRedirectedPath(origPath);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
        return origPath;
    }

    public static String resverseRedirectedPath(String origPath) {
        try {
            return nativeReverseRedirectedPath(origPath);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
        return origPath;
    }

    public static void redirectDirectory(String origPath, String newPath) {
        if (!origPath.endsWith("/")) {
            origPath = origPath + "/";
        }
        if (!newPath.endsWith("/")) {
            newPath = newPath + "/";
        }
        try {
            nativeIORedirect(origPath, newPath);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    public static void redirectFile(String origPath, String newPath) {
        if (origPath.endsWith("/")) {
            origPath = origPath.substring(0, origPath.length() - 1);
        }
        if (newPath.endsWith("/")) {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        try {
            nativeIORedirect(origPath, newPath);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    public static void whitelist(String path) {
        try {
            nativeIOWhitelist(path);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    public static void forbid(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        try {
            nativeIOForbid(path);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    public static void enableIORedirect() {
        try {
            String soPath = String.format("/data/data/%s/lib/libva++.so", VirtualCore.get().getHostPkg());
            if (!new File(soPath).exists()) {
                throw new RuntimeException("Unable to find the so.");
            }
            nativeEnableIORedirect(soPath, Build.VERSION.SDK_INT, BuildCompat.getPreviewSDKInt());
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
    }

    static void launchEngine() {
        if (sFlag) {
            return;
        }

        Method[] methods = {NativeMethods.gOpenDexFileNative, NativeMethods.gCameraNativeSetup,
            NativeMethods.gAudioRecordNativeCheckPermission};
        try {
            nativeLaunchEngine(methods, VirtualCore.get().getHostPkg(), VirtualRuntime.isArt(), Build.VERSION.SDK_INT,
                NativeMethods.gCameraMethodType);
        } catch (Throwable e) {
            VLog.e(TAG, VLog.getStackTraceString(e));
        }
        hookNetwork();
        sFlag = true;
    }

    private static void hookNetwork() {
        socket = new Socket();
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
        nativeSetVpnFd(ParcelFileDescriptor.fromSocket(socket).getFd());
        VpnController vpnController = new VpnController(null, new IVpnService() {
            @Override
            public boolean protect(int socket) {
                return nativeProtect(socket);
            }

            @Override
            public boolean protect(Socket socket) {
                return protect(ParcelFileDescriptor.fromSocket(socket).getFd());
            }

            @Override
            public boolean protect(DatagramSocket socket) {
                return protect(ParcelFileDescriptor.fromDatagramSocket(socket).getFd());
            }

            @Override
            public FileDescriptor getInterceptFd() {
                return pfd.getFileDescriptor();
            }
        });
        vpnController.startLocalVPN();
    }



    public static void onKillProcess(int pid, int signal) {
        VLog.e(TAG, "killProcess: pid = %d, signal = %d.", pid, signal);
        if (pid == android.os.Process.myPid()) {
            VLog.e(TAG, VLog.getStackTraceString(new Throwable()));
        }
    }

    public static int onGetCallingUid(int originUid) {
        int callingPid = Binder.getCallingPid();
        if (callingPid == Process.myPid()) {
            return VClientImpl.get().getBaseVUid();
        }
        if (callingPid == VirtualCore.get().getSystemPid()) {
            return Process.SYSTEM_UID;
        }
        int vuid = VActivityManager.get().getUidByPid(callingPid);
        if (vuid != -1) {
            return VUserHandle.getAppId(vuid);
        }
        VLog.d(TAG, "Unknown uid: " + callingPid);
        return VClientImpl.get().getBaseVUid();
    }

    public static void onOpenDexFileNative(String[] params) {
        String dexOrJarPath = params[0];
        String outputPath = params[1];
        VLog.d(TAG, "DexOrJarPath = %s, OutputPath = %s.", dexOrJarPath, outputPath);
        try {
            String canonical = new File(dexOrJarPath).getCanonicalPath();
            InstalledAppInfo info = sDexOverrideMap.get(canonical);
            if (info != null && !info.dependSystem) {
                outputPath = info.getOdexFile().getPath();
                params[1] = outputPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect(int fd) {
        Log.d("baba", "hook the fd" + fd);
    }

    private static native void nativeLaunchEngine(Object[] method, String hostPackageName, boolean isArt, int apiLevel,
                                                  int cameraMethodType);

    private static native void nativeMark();

    private static native String nativeReverseRedirectedPath(String redirectedPath);

    private static native String nativeGetRedirectedPath(String orgPath);

    private static native void nativeIORedirect(String origPath, String newPath);

    private static native void nativeIOWhitelist(String path);

    private static native void nativeIOForbid(String path);

    private static native void nativeEnableIORedirect(String selfSoPath, int apiLevel, int previewApiLevel);

    public static int onGetUid(int uid) {
        return VClientImpl.get().getBaseVUid();
    }

    private static native boolean nativeProtect(int fd);

    private static native void nativeSetVpnFd(int fd);
}
