package com.lc.puppet.storage.transfers;

import android.os.Bundle;

import com.lc.puppet.IObjectWrapper;

import java.util.Map;


/**
 * @author legency
 */
public class CellLocationTransferServer implements ServerTransfer<Bundle,Map<String,Object>> {
    @Override
    public Map<String, Object> transferBeforeSave(IObjectWrapper<Bundle> o) {
        Bundle bundle = o.get();
        //需要先进行数据序列化到 map
        mirror.android.os.Bundle.BaseBundle.unparcel.call(bundle);
        return mirror.android.os.Bundle.BaseBundle.mMap.get(bundle);
    }

    @Override
    public Bundle transferAfterRead(Map<String, Object> o) {
        android.os.Bundle bundle = new android.os.Bundle();
        mirror.android.os.Bundle.BaseBundle.mMap.set(bundle,o);
        return bundle;
    }

}
