package com.lc.puppet.service.providers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.lc.puppet.proto.WifiInfoMirror;
import com.lc.puppet.service.providers.base.HookDataProvider;
import com.lc.puppet.service.providers.base.IOBHookDataWithFake;
import com.lc.puppet.service.providers.base.PatchHookProvider;
import com.lc.puppet.storage.IObIndex;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.wifi.WifiManagerStub;
import com.lody.virtual.helper.utils.Reflect;

public class WifiManagerProvider extends PatchHookProvider {

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return WifiManagerStub.class;
    }

    @Override
    protected void addHookDataProviders() {
        addProvider(new getConnectionInfo());
        addProvider(new getScanResults());
        addProvider(new getWifiEnabledState());
    }

    private class getConnectionInfo extends IOBHookDataWithFake<WifiInfo> {

        @Override
        public String getName() {
            return "getConnectionInfo";
        }

        @Override
        public WifiInfo createFakeData() {
            return getFakeWifiInfo();
        }

        @Override
        public IObIndex getIObIndex() {
            return IObIndex.WIFI_INFO;
        }
    }

    /**
     * @author legency
     */
    private class getScanResults extends IOBHookDataWithFake<List<ScanResult>> {

        @Override
        public String getName() {
            return "getScanResults";
        }

        @Override
        public List<ScanResult> createFakeData() {
            return createFakeScanResults();
        }

        @Override
        public IObIndex getIObIndex() {
            return IObIndex.SCAN_RESULTS;
        }
    }

    private class getWifiEnabledState extends IOBHookDataWithFake<Integer>{

        @Override
        public String getName() {
            return "getWifiEnabledState";
        }

        @Override
        public Integer createFakeData() {
            return WifiManager.WIFI_STATE_ENABLED;
        }

        @Override
        public IObIndex getIObIndex() {
            return IObIndex.WIFI_STATE;
        }
    }

    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetConnectionInfo
     */
    @ForReflect
    private WifiInfo getConnectionInfo() {
        return callDataWithCreator(IObIndex.WIFI_INFO, new PaperDataCreator<WifiInfo>() {
            @Override
            public WifiInfo createFakeData() {
                return getFakeWifiInfo();

            }
        });
    }

    private WifiInfo getFakeWifiInfo() {
        String hostName = null;
        byte[] ipAddress = {100, 84, -55, -103};
        int family = 2;
        InetAddress inetAddress = WifiInfoMirror.InetAddressL.getCtor().newInstance(family, ipAddress, hostName);
        return new WifiInfoMirror.Builder().setFrequency(5825).setMacAddress("02:00:00:00:00:00")
                .setBSSID("00:00:00:00:00:00").setEphemeral(false).setLinkSpeed(72)
                .setMeteredHint(false).setNetworkId(7).setRssi(-55)
                .setSupplicantState(SupplicantState.COMPLETED)
                .setIpAddress(inetAddress).setWifiSsid("test_ssid").create();
    }

    /**
     * @param callingPackage
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetScanResults
     */
    @ForReflect
    List<ScanResult> getScanResults(String callingPackage) {
        return callDataWithCreator(IObIndex.SCAN_RESULTS, new PaperDataCreator<List<ScanResult>>() {
            //


            @Override
            public List<ScanResult> createFakeData() {
                return createFakeScanResults();
            }


        });
    }

    String[] BSSIDS = {"00:00:00:00:00:00"};

    private List<ScanResult> createFakeScanResults() {
        List<ScanResult> scanResults = new ArrayList<>();
        for (String bssid : BSSIDS) {
            ScanResult scanResult = Reflect.on(ScanResult.class).create().get();
            scanResult.SSID = "test_ssid";
            scanResult.BSSID = bssid;
            scanResults.add(scanResult);
        }
        return scanResults;
    }

    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.wifi.Interceptor_GetWifiEnabledState
     */
    @ForReflect
    public int getWifiEnabledState() {
        return WifiManager.WIFI_STATE_ENABLED;
    }
}
