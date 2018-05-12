package com.minhui.vpn;

import java.io.FileDescriptor;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

/**
 * @author legency
 * @date 2018/05/12.
 */
public class VpnController {

    private static final String TAG = com.minhui.vpn.LocalVPNService.class.getSimpleName();
    public static final String BROADCAST_VPN_STATE = "com.minhui.localvpn.VPN_STATE";
    private static VpnController instance;

    private FileDescriptor mTargetFd = null;

    private ConcurrentLinkedQueue<Packet> networkToDeviceQueue;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private Selector selector;
    private static boolean isRunning = false;
    private VPNServer vpnServer;
    private VPNClient vpnInPutRunnable;
    private String selectPackage;
    private IVpnService iVpnService;
    private Context context;

    public static boolean isRunning() {
        return isRunning;
    }

    public String getSelectPackage() {
        return selectPackage;
    }

    public VpnController(Context context, IVpnService iVpnService) {
        this.context = context;
        this.iVpnService = iVpnService;
    }

    public VpnController(FileDescriptor fileDescriptor) {
        this.mTargetFd = fileDescriptor;
    }

    public void startLocalVPN() {
        if (isRunning()) {
            return;
        }
        try {
            isRunning = true;
            instance = this;
            selector = Selector.open();
            networkToDeviceQueue = new ConcurrentLinkedQueue<>();
            mTargetFd = iVpnService.getInterceptFd();
            vpnServer = new VPNServer(iVpnService, networkToDeviceQueue, selector);
            vpnInPutRunnable = new VPNClient(mTargetFd, vpnServer, networkToDeviceQueue,
                selector);
            executorService.submit(vpnInPutRunnable);
            executorService.submit(vpnServer);
            if (context != null) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_VPN_STATE));
            }
            Log.i(TAG, "Started");
            VPNConnectManager.getInstance().setLastVpnStartTime(System.currentTimeMillis());
            if (context != null) {
                PortHostService.startParse(context.getApplicationContext());
            }
        } catch (Exception e) {
            Log.w(TAG, "Error starting service", e);
            cleanup();
        }
    }

    public void cleanup() {
        if (!isRunning()) {
            return;
        }
        Log.i(TAG, "clean up");
        isRunning = false;
        networkToDeviceQueue = null;
        PortHostService.getInstance().getAndRefreshConnInfo();
        PortHostService.stopParse(context.getApplicationContext());
        closeRunnable(vpnServer);
        closeRunnable(vpnInPutRunnable);
        try {
            Os.close(mTargetFd);
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
        if (context != null) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_VPN_STATE));
        }

        instance = null;
    }

    private void closeRunnable(CloseableRun run) {
        if (run != null) {
            run.closeRun();
        }
    }

    public VPNServer getVpnServer() {
        return vpnServer;
    }

    public static VpnController getInstance() {
        return instance;
    }

}
