package com.lc.puppet.storage.transfers;

import com.lc.puppet.IObjectWrapper;

/**
 * Created by lichen:) on 2016/9/12.
 */
public interface ServerTransfer<ORI_BOJ, SAVED> {
    SAVED transferBeforeSave(IObjectWrapper<ORI_BOJ> o);
    ORI_BOJ transferAfterRead(SAVED o);
}

