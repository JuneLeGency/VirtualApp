package com.lc.puppet.service.providers;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lc.puppet.proto.NetworkInfoMirror;
import com.lc.puppet.service.providers.base.IOBHookDataWithFake;
import com.lc.puppet.service.providers.base.PatchHookProvider;
import com.lc.puppet.storage.IObIndex;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.connectivity.ConnectivityStub;

/**
 * @author legency
 */
public class ConnectivityProvider extends PatchHookProvider {

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return ConnectivityStub.class;
    }

    @Override
    protected void addHookDataProviders() {
        addProvider(new getActiveNetworkInfo());
    }


    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.connectivity.Interceptor_GetActiveNetworkInfo
     */
    private static class getActiveNetworkInfo extends IOBHookDataWithFake<NetworkInfo> {

        @Override
        public NetworkInfo createFakeData() {
            return createFakeNetworkInfo();
        }

        @Override
        public IObIndex getIObIndex() {
            return IObIndex.NETWORK_INFO;
        }

        @Override
        public String getName() {
            return "getActiveNetworkInfo";
        }
    }


    private NetworkInfo getActiveNetworkInfo() {
        return callDataWithCreator(IObIndex.NETWORK_INFO, new PaperDataCreator<NetworkInfo>() {
            @Override
            public NetworkInfo createFakeData() {
                return createFakeNetworkInfo();
            }
        });
    }

    private static NetworkInfo createFakeNetworkInfo() {
        return new NetworkInfoMirror.Builder().setNetworkType(ConnectivityManager.TYPE_WIFI).setSubtype(0)
                .setTypeName("WIFI").setAvailable(true).
                        setDetailedState(NetworkInfo.DetailedState.CONNECTED).
                        setExtraInfo("wifi_test_name").create();
    }
}
