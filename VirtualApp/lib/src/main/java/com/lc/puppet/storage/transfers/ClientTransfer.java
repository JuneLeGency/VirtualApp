package com.lc.puppet.storage.transfers;

import com.lc.puppet.IObjectWrapper;

/**
 *  cause some api data is different from the aidl interfaces so
 *  transferToProxyObj data before save is needed
 *  et. android.telephony.TelephonyManager#getCellLocation() returns CellLocation
 *  ITelephony.getCellLocation return Bundle
 * @author legency
 */
public interface ClientTransfer<PROXY_OBJ,API_OBJ> {
    IObjectWrapper<PROXY_OBJ> transferToProxyObj(API_OBJ object);
    API_OBJ transferToApiObj(IObjectWrapper<PROXY_OBJ> object);

}
