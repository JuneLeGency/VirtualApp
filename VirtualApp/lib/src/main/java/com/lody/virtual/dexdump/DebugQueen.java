package com.lody.virtual.dexdump;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;

import android.util.Log;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.helper.utils.Reflect;
import com.taobao.android.dexposed.ClassUtils;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

/**
 * @author legency
 * @date 2018/06/06.
 */
public class DebugQueen {
    private static boolean hasHook;

    public static void debug() {
        if (hasHook) {
            return;
        }
        Log.d("asdasdasd","1");
        //if (VClientImpl.get().getCurrentApplication() == null) { return; }
        Log.d("asdasdasd","2");
        //boolean queenEnable = Reflect.on("com.tencent.common.http.QueenConfig", getClassLoader()).call(
        //    "isQueenProxyEnable").get();
        //Log.d("queenDebug", "isQueenProxyEnable:" + queenEnable);
        hookRequestCall();
        Log.d("asdasdasd","3");
        hasHook = true;
    }

    private static ClassLoader getClassLoader() {
        return VClientImpl.get().getCurrentApplication().getClassLoader();
    }

    static void hookRequestCall() {
        try {
            DexposedBridge.hookAllConstructors(Class.forName("com.android.okhttp.OkHttpClient"), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("asd", "asd", new Throwable());
                    super.afterHookedMethod(param);
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //DexposedBridge.findAndHookMethod(WebView.class,
        //    "loadUrl", String.class,
        //    new XC_MethodHook() {
        //        @Override
        //        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //            Log.d("queenDebug", "WebView loadUrl " + param.args[0].toString());
        //        }
        //    });
        //hookIO();
        //queenHook();
        //proxynewHook();
        //openConnectionHook();
        //okHttpHook();
    }

    private static void proxynewHook() {
        DexposedBridge.hookAllConstructors(Proxy.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d("queenDebug",
                    "Proxy new  " + (param.getResult() == null ? "null" : param.getResult().toString()));
            }
        });
    }

    private static void openConnectionHook() {
        DexposedBridge.findAndHookMethod(URL.class,
            "openConnection",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("queenDebug", "URL openConnection url " + param.thisObject.toString());
                }
            });
        DexposedBridge.findAndHookMethod(URL.class,
            "openConnection", Proxy.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("queenDebug", "URL openConnection proxy url " + param.thisObject.toString());
                }
            });
    }

    private static void okHttpHook() {
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils.getClass(getClassLoader(), "okhttp3.OkUrlFactory", false),
                "open",
                URL.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("queenDebug", "OkUrlFactory open" + param.args[0].toString());
                    }
                });
            Log.d("queenDebug", "OkUrlFactory hooksuccess");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils.getClass(getClassLoader(), "okhttp3.OkUrlFactory", false),
                "open",
                URL.class, Proxy.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("queenDebug", "OkUrlFactory open proxy" + param.args[0].toString());
                    }
                });
            Log.d("queenDebug", "OkUrlFactory hooksuccess");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void queenHook() {
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils
                    .getClass(getClassLoader(), "com.tencent.common.http.QueenConfig", false),
                "getIpList",
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("queenDebug", "getIpList:" + param.getResult());
                    }
                });
            Log.d("queenDebug", "QueenConfig getIpList hooked");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils
                    .getClass(getClassLoader(), "com.tencent.mtt.businesscenter.config.QueenInfoProviderImpl", false),
                "handleCmd",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("queenDebug", "handleCmd:" + param.args[0]);
                    }
                });
            Log.d("queenDebug", "QueenInfoProviderImpl handleCmd hooked");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils.getClass(getClassLoader(), "com.tencent.common.http.QueenConfig", false),
                "handleRequest",
                "com.tencent.common.http.MttRequestBase", int.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String url = Reflect.on(param.args[0]).call(
                            "getUrl").get();
                        boolean IsWupRequest = Reflect.on(param.args[0]).call(
                            "getIsWupRequest").get();
                        Log.d("queenDebug", "handleRequest:" + IsWupRequest + url);
                    }
                });
            Log.d("queenDebug", "MttRequestBase hooksuccess");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils.getClass(getClassLoader(), "com.tencent.common.http.QueenConfig", false),
                "getBusinessName",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("queenDebug", "getBusinessName:" + param.getResult());
                    }
                });
            Log.d("queenDebug", "QueenConfig getBusinessName");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DexposedBridge.findAndHookMethod(
                ClassUtils.getClass(getClassLoader(), "com.tencent.common.http.OkHttpQueenConfig", false),
                "handleRequest",
                "okhttp3.OkHttpClient", "okhttp3.Request", int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String url = Reflect.on(param.args[1]).call(
                            "url").get();
                        Log.d("queenDebug", "ok handleRequest:" + url);
                    }
                });
            Log.d("queenDebug", "OkHttpQueenConfig hooksuccess");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void hookIO() {
        DexposedBridge.findAndHookMethod(OutputStream.class, "write", byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String s = new String(
                    (byte[])param.args[0]);
                if (param.args.length > 0) {
                    Log.d("queenDebug",
                        "OutputStream write:" + param.thisObject.getClass().getCanonicalName() + s);
                    if (s.contains("httpbin.org")) {
                        Log.e("queenDebug", "sd", new Throwable());
                    }
                }
            }
        });

        DexposedBridge.findAndHookMethod(InputStream.class, "read", byte[].class, int.class, int.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String s = new String(
                        (byte[])param.args[0]);
                    if (param.args.length > 0) {
                        Log.d("queenDebug",
                            "InputStream read int int:" + param.thisObject.getClass().getCanonicalName() + s);
                    }
                }
            });
        DexposedBridge.findAndHookMethod(InputStream.class, "read", byte[].class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String s = new String(
                    (byte[])param.args[0]);
                if (param.args.length > 0) {
                    Log.d("queenDebug",
                        "InputStream read:" + param.thisObject.getClass().getCanonicalName() + s);
                }
            }
        });
    }
}
