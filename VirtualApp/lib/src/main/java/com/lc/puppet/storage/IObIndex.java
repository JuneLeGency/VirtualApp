package com.lc.puppet.storage;

import android.location.Location;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.telephony.CellLocation;

import com.lc.puppet.storage.transfers.CellLocationTransferClient;
import com.lc.puppet.storage.transfers.CellLocationTransferServer;
import com.lc.puppet.storage.transfers.ClientTransfer;
import com.lc.puppet.storage.transfers.ServerTransfer;

import java.lang.reflect.Field;
import java.util.List;

/**
 * the mockdata index
 * annotation for exec support by aidl
 * <p>
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

    IObType getIObType() throws NoSuchFieldException {
        return asFiled().getAnnotation(IObType.class);
    }

    public ClientTransfer getClientTransfer(){
        try {
            Class<? extends ClientTransfer> clientTransferClazz =  getIObType().clientTransfer();
            if (clientTransferClazz != ClientTransfer.class) {
                return clientTransferClazz.newInstance();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServerTransfer getServerTransfer(){
        try {
            Class<? extends ServerTransfer> serverTransfer =  getIObType().serverTransfer();
            return serverTransfer.newInstance();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
