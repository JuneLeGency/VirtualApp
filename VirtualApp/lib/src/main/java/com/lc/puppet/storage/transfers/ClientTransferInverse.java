package com.lc.puppet.storage.transfers;


/**
 *  cause some api data is different from the aidl interfaces so
 *  transferToProxyObj data before save is needed
 *  et. android.telephony.TelephonyManager#getCellLocation() returns CellLocation
 *  ITelephony.getCellLocation return Bundle
 * @author legency
 */
public interface ClientTransferInverse<PROXY_OBJ,API_OBJ> {
    API_OBJ transfer(PROXY_OBJ object);
}
