package com.lc.puppet.storage;

import android.location.Location;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.telephony.CellLocation;

import com.lc.puppet.storage.transfers.CellLocationTransferClient;
import com.lc.puppet.storage.transfers.CellLocationTransferServer;

import java.lang.reflect.Field;
import java.util.List;

/**
 * the mockdata index
 * annotation for exec support by aidl
 *
 * Created by legency on 2016/9/13.
 */
public enum IObIndex {

    @IObType(Location.class)
    LOCATION,

    @IObType(android.net.NetworkInfo.class)
    NETWORK_INFO,

    @IObType(List.class)
    CELL_INFOS,

    @IObType(value = Bundle.class, clientTransfer = CellLocationTransferClient.class)  // ,serverTransfer = CellLocationTransferServer.class )
    CELL_LOCATION,

    @IObType(List.class)
    NEIGHBORING_CELL_INFOS,

    @IObType(WifiInfo.class)
    WIFI_INFO,

    @IObType(List.class)
    SCAN_RESULTS,

    @IObType(int.class)
    WIFI_STATE,

    @IObType(int.class)
    PHONE_TYPE;

    public Field asFiled() throws NoSuchFieldException {
        return IObIndex.class.getField(this.name());
    }
}
