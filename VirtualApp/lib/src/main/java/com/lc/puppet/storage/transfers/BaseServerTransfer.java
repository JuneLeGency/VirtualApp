package com.lc.puppet.storage.transfers;

import com.lc.puppet.IObjectWrapper;

/**
 * Created by lichen:) on 2016/9/12.
 */
public class BaseServerTransfer<ORI_BOJ, SAVED>  implements ServerTransfer<ORI_BOJ, SAVED>  {
    @Override
    public SAVED transferBeforeSave(IObjectWrapper<ORI_BOJ> o) {
        return (SAVED) o.get();
    }

    @Override
    public ORI_BOJ transferAfterRead(SAVED o) {
        return (ORI_BOJ) o;
    }
}
