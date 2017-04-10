package com.lc.puppet.client.hook.patch.hook.telephony;

/**
 * @author Junelegency
 *
 */
public class Interceptor_GetActivePhoneTypeForSlot extends BaseInterceptorTelephony {
    @Override
    public String getMethodName() {
        return "getActivePhoneTypeForSlot";
    }

}
