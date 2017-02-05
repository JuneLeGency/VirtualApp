package io.virtualapp;

import android.app.Application;
import android.content.Context;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.stub.StubManifest;

import io.virtualapp.abs.ui.VActivity;
import jonathanfinerty.once.Once;
import me.drakeet.library.CrashWoodpecker;
import me.drakeet.library.PatchMode;

/**
 * @author Lody
 */
public class VApp extends Application {

    private static VApp gDefault;

    public static VApp getApp() {
        return gDefault;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            StubManifest.ENABLE_IO_REDIRECT = true;
            VirtualCore.get().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        gDefault = this;
        super.onCreate();
        if (VirtualCore.get().isMainProcess()) {
            Once.initialise(this);
        } else if (VirtualCore.get().isVAppProcess()) {
            VirtualCore.get().setComponentDelegate(new MyComponentDelegate());
            VirtualCore.get().setPhoneInfoDelegate(new MyPhoneInfoDelegate());
            VirtualCore.get().setTaskDescriptionDelegate(new MyTaskDescriptionDelegate());
        }
        CrashWoodpecker.instance()
                .withKeys("widget", "me.drakeet")
                .setPatchMode(PatchMode.SHOW_LOG_PAGE)
                .setPatchDialogUrlToOpen("https://drakeet.me")
                .setPassToOriginalDefaultHandler(true)
                .flyTo(this);
    }

}
