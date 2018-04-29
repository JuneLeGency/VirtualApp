package com.lody.virtual.dexdump;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import android.util.Log;

/**
 * @author legency
 * @date 2018/04/29.
 */
public class ProxyClassLoader extends ClassLoader {

    public ProxyClassLoader(ClassLoader target) {
        super(target);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Log.d("asdasd", "loadClass(String name, boolean resolve)" + name);
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Log.d("asdasd", "findClass(String name) " + name);
        return super.findClass(name);
    }

    @Override
    public URL getResource(String name) {
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return super.getResources(name);
    }

    @Override
    protected URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return super.findResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    @Override
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor,
                                    String implTitle, String implVersion, String implVendor, URL sealBase)
        throws IllegalArgumentException {
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor,
            sealBase);
    }

    @Override
    protected Package getPackage(String name) {
        return super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {
        return super.getPackages();
    }

    @Override
    protected String findLibrary(String libname) {
        return super.findLibrary(libname);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        super.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        super.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        super.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        super.clearAssertionStatus();
    }
}
