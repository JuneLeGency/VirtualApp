package com.lc.puppet.storage.transfers;

import com.lc.puppet.IObjectWrapper;

/**
 *  cause some api data is different from the aidl interfaces so
 *  transfer data before save is needed
 *  et. android.telephony.TelephonyManager#getCellLocation() returns CellLocation
 *  ITelephony.getCellLocation return Bundle
 * @author legency
 */
public interface ClientTransfer<T> {
    IObjectWrapper<T> transfer(Object object);
}
