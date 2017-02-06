package com.lc.puppet.storage.transfers;

import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.lc.puppet.IObjectWrapper;

/**
 * @author legency
 */
public class CellLocationTransferClient implements ClientTransfer<Bundle,CellLocation> {

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

    @Override
    public IObjectWrapper<Bundle> transferToProxyObj(CellLocation object) {
        IObjectWrapper<Bundle> wrapper = new IObjectWrapper<>();
        if (object instanceof GsmCellLocation) {
            wrapper.setParcelable(createGsmCellLocation((GsmCellLocation) object));
        }
        if (object instanceof CdmaCellLocation) {
            wrapper.setParcelable(createCdmaCellLocation((CdmaCellLocation) object));
        }
        return wrapper;
    }

    @Override
    public CellLocation transferToApiObj(IObjectWrapper<Bundle> object) {
        return mirror.android.telephony.CellLocation.newFromBundle.call(object.get());
    }
}
