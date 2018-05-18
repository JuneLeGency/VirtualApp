https://github.com/rofl0r
- hook impl
https://github.com/rofl0r/rocksock
- server
https://github.com/rofl0r/microsocks/
- proxy server
https://github.com/tinyproxy/tinyproxy
- hook socket
https://github.com/rofl0r/proxychains-ng
- http server
https://github.com/rofl0r/rocksock-httpd
- SOCKS v5 server
https://github.com/rofl0r/rocksocks5
-compile c
https://github.com/rofl0r/rcb
- c lib
https://github.com/rofl0r/libulz
https://github.com/tinyproxy/tinyproxy

https://github.com/ac-pm/ProxyOn/blob/master/app/src/main/java/mobi/acpm/proxyon/Module.java
```java
    findAndHookMethod("java.net.ProxySelectorImpl", loadPackageParam.classLoader, "select", URI.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if(sPrefs.getBoolean("switch", false)) {

                    System.setProperty("proxyHost", sPrefs.getString("host", null));
                    System.setProperty("proxyPort", sPrefs.getString("port", null));

                    System.setProperty("http.proxyHost", sPrefs.getString("host", null));
                    System.setProperty("http.proxyPort", sPrefs.getString("port", null));

                    System.setProperty("https.proxyHost", sPrefs.getString("host", null));
                    System.setProperty("https.proxyPort", sPrefs.getString("port", null));

                    System.setProperty("socksProxyHost", sPrefs.getString("host", null));
                    System.setProperty("socksProxyPort", sPrefs.getString("port", null));
                }
            }
        });

        findAndHookMethod("java.net.ProxySelectorImpl", loadPackageParam.classLoader, "isNonProxyHost", String.class, String.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if(sPrefs.getBoolean("switch", false)) {
                    param.args[1] = "";
                }
            }
        });
 ```