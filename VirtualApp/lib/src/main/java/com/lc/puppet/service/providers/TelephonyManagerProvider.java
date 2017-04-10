package com.lc.puppet.service.providers;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.lc.puppet.proto.CellInfoMirror;
import com.lc.puppet.service.providers.base.PatchHookProvider;
import com.lc.puppet.storage.IObIndex;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.proxies.telephony.TelephonyStub;
import com.lody.virtual.helper.utils.Reflect;


/**
 * @author legency
 */
public class TelephonyManagerProvider extends PatchHookProvider {

    @Override
    public Class<? extends MethodInvocationProxy> getDelegatePatch() {
        return TelephonyStub.class;
    }

    /**
     * Returns a constant indicating the device phone type for a subscription.
     *
     * @see TelephonyManager#PHONE_TYPE_GSM
     * @see TelephonyManager#PHONE_TYPE_CDMA
     * @see TelephonyManager#PHONE_TYPE_NONE
     * @see android.telephony.TelephonyManager#PHONE_TYPE_SIP
     * @see com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetActivePhoneTypeForSubscriber
     */
    public int getActivePhoneTypeForSubscriber() {
        return getPhoneTypeInner();
    }

    /**
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetActivePhoneTypeForSlot
     */
    public int getActivePhoneTypeForSlot() {
        return getPhoneTypeInner();
    }

    private int getPhoneTypeInner() {
        return callDataWithCreator(IObIndex.PHONE_TYPE, new PaperDataCreator<Integer>() {
            @Override
            public Integer createFakeData() {
                return TelephonyManager.PHONE_TYPE_GSM;
            }
        });
    }


    List<CellInfo> getAllCellInfo(String pkg) {
        return callDataWithCreator(IObIndex.CELL_INFOS, new PaperDataCreator<List<CellInfo>>() {
            @Override
            public List<CellInfo> createFakeData() {
                List<CellInfo> list = new ArrayList<>();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            int uid =2147483647;
                    int uid = 123;
                    //主要关心tac ci 值
                    CellIdentityLte[] cellIdentityLtes = {Reflect.on(CellIdentityLte.class).create(460, 1, 111210241, 21, 9673).get(),
//                    new CellIdentityLte(uid, uid, uid, 77, uid),
//                    new CellIdentityLte(uid, uid, uid, 43, uid),
//                    new CellIdentityLte(uid, uid, uid, 303, uid),
//                    new CellIdentityLte(uid, uid, uid, 395, uid),
//                    new CellIdentityLte(uid, uid, uid, 187, uid)
                    };
                    for (CellIdentityLte c : cellIdentityLtes) {
                        CellInfoLte cellInfoLte = CellInfoMirror.CellInfoLteMirror.ctor.newInstance();
                        CellInfoMirror.CellInfoLteMirror.mCellIdentityLte.set(cellInfoLte, c);
                        CellInfoMirror.mRegistered.set(cellInfoLte, false);
                        list.add(cellInfoLte);
                    }
                }
                return list;
            }
        });
    }

    /**
     * @param subId
     * @return
     * @see com.lc.puppet.client.hook.patch.hook.telephony.Interceptor_GetAllCellInfoUsingSubId
     */
    public List<CellInfo> getAllCellInfoUsingSubId(int subId) {
        return getAllCellInfo(null);
    }

    /**
     * 和 @see TelephonyManager#PHONE_TYPE_GSM 一一对应
     *
     * @return
     */
    public Bundle getCellLocation() {
        return callDataWithCreator(IObIndex.CELL_LOCATION, new PaperDataCreator<Bundle>() {
            @Override
            public Bundle createFakeData() {
                return getCellLocationInner();
            }
        });
    }

    public static  Bundle CellLocationToBundle(Object o) {
        if (o instanceof GsmCellLocation) {
            return createGsmCellLocation((GsmCellLocation) o);
        }
        if (o instanceof CdmaCellLocation) {
            return createCdmaCellLocation((CdmaCellLocation) o);
        }
        return null;
    }

    private Bundle getCellLocationInner() {
        switch (getActivePhoneTypeForSlot()) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                return createCdmaCellLocation(null);
            case TelephonyManager.PHONE_TYPE_GSM:
                return createGsmCellLocation(null);
            default:
                return null;
        }
    }

    List<NeighboringCellInfo> getNeighboringCellInfo() {
        return callDataWithCreator(IObIndex.NEIGHBORING_CELL_INFOS, new PaperDataCreator<List<NeighboringCellInfo>>() {
            @Override
            public List<NeighboringCellInfo> createFakeData() {
                List<NeighboringCellInfo> cellInfo = new ArrayList<>();
                NeighboringCellInfo n = new NeighboringCellInfo(3, "6156", TelephonyManager.NETWORK_TYPE_HSDPA);
                n.setCid(123);
                n.setRssi(132);
                cellInfo.add(n);
                return cellInfo;
            }
        });
    }

    private static Bundle createGsmCellLocation(GsmCellLocation gsmCellLocation) {
        int lac = 9500;
        int cid = 101010691;
        int psc = -1;
        if (gsmCellLocation != null) {
            lac = gsmCellLocation.getLac();
            cid = gsmCellLocation.getCid();
            psc = gsmCellLocation.getPsc();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("lac", lac);
        bundle.putInt("cid", cid);
        bundle.putInt("psc", psc);
        return bundle;
    }

    private static Bundle createCdmaCellLocation(CdmaCellLocation cdmaCellLocation) {
        int baseStationId = 9500;
        int baseStationLatitude = 101010691;
        int baseStationLongitude = -1;
        int systemId = -1;
        int networkId = -1;
        if (cdmaCellLocation != null) {
            baseStationId = cdmaCellLocation.getBaseStationId();
            baseStationLatitude = cdmaCellLocation.getBaseStationLatitude();
            baseStationLongitude = cdmaCellLocation.getBaseStationLongitude();
            systemId = cdmaCellLocation.getSystemId();
            networkId = cdmaCellLocation.getNetworkId();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("baseStationId", baseStationId);
        bundle.putInt("baseStationLatitude", baseStationLatitude);
        bundle.putInt("baseStationLongitude", baseStationLongitude);
        bundle.putInt("systemId", systemId);
        bundle.putInt("networkId", networkId);
        return bundle;
    }
}
