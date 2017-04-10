package com.lc.puppet.service.providers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.lc.puppet.proto.WifiInfoMirror;
import com.lc.puppet.service.providers.base.PatchHookProvider;
import com.lc.puppet.storage.IObIndex;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.wifi.WifiManagerStub;
import com.lody.virtual.helper.utils.Reflect;

/**
 * @author legency
 */
public class WifiManagerProvider extends PatchHookProvider {

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return WifiManagerStub.class;
    }

    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetConnectionInfo
     */
    private WifiInfo getConnectionInfo() {
        return callDataWithCreator(IObIndex.WIFI_INFO, new PaperDataCreator<WifiInfo>() {
            @Override
            public WifiInfo createFakeData() {
                String hostName = null;
                byte[] ipAddress = {100, 84, -55, -103};
                int family = 2;
                InetAddress inetAddress = WifiInfoMirror.InetAddressL.ctor.newInstance(family, ipAddress, hostName);
                return new WifiInfoMirror.Builder().setFrequency(5825).setMacAddress("02:00:00:00:00:00")
                        .setBSSID("00:00:00:00:00:00").setEphemeral(false).setLinkSpeed(72)
                        .setMeteredHint(false).setNetworkId(7).setRssi(-55)
                        .setSupplicantState(SupplicantState.COMPLETED)
                        .setIpAddress(inetAddress).setWifiSsid("test_ssid").create();

            }
        });
    }

    /**
     * @param callingPackage
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetScanResults
     */
    List<ScanResult> getScanResults(String callingPackage) {
        return callDataWithCreator(IObIndex.SCAN_RESULTS, new PaperDataCreator<List<ScanResult>>() {
            //
            String[] BSSIDS = {"00:00:00:00:00:00"};

            @Override
            public List<ScanResult> createFakeData() {
                List<ScanResult> scanResults = new ArrayList<>();
                for (String bssid : BSSIDS) {
                    ScanResult scanResult = Reflect.on(ScanResult.class).create().get();
                    scanResult.SSID = "test_ssid";
                    scanResult.BSSID = bssid;
                    scanResults.add(scanResult);
                }
                return scanResults;
            }
        });
    }

    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetWifiEnabledState
     */
    public int getWifiEnabledState() {
        return WifiManager.WIFI_STATE_ENABLED;
    }


}
