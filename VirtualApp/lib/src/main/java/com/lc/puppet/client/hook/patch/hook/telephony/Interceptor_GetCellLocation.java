package com.lc.puppet.client.hook.patch.hook.telephony;

/**
 * 获取单个基站信息
 *
 * @author legency
 */
public class Interceptor_GetCellLocation extends BaseInterceptorTelephony {

    @Override
    public String getName() {
        return "getCellLocation";
    }
}
