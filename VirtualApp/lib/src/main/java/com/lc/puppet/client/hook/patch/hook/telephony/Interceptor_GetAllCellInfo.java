package com.lc.puppet.client.hook.patch.hook.telephony;

/**
 * 获取基站信息
 *
 * @author legency
 */
public class Interceptor_GetAllCellInfo extends BaseInterceptorTelephony {

    @Override
    public String getMethodName() {
        return "getAllCellInfo";
    }

}
