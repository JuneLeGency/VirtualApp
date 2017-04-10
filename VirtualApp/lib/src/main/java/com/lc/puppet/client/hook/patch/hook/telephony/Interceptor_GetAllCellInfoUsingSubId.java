package com.lc.puppet.client.hook.patch.hook.telephony;

/**
 * 获取基站 信息
 *
 * @author legency
 */
public class Interceptor_GetAllCellInfoUsingSubId extends BaseInterceptorTelephony {

    @Override
    public String getMethodName() {
        return "getAllCellInfoUsingSubId";
    }

}
