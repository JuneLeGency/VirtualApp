package com.lc.puppet.client.hook.patch.hook.telephony;

/**
 * @author Junelegency
 */
public class Interceptor_GetNeighboringCellInfo extends BaseInterceptorTelephony {

    @Override
    public String getMethodName() {
        return "getNeighboringCellInfo";
    }
}
