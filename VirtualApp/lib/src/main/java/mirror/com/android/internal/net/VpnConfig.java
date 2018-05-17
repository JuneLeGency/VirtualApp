package mirror.com.android.internal.net;

import mirror.RefClass;
import mirror.RefObject;

/**
 * @author legency
 * @date 2018/05/14.
 */
public class VpnConfig {
    public static Class<?> TYPE = RefClass.load(VpnConfig.class, "com.android.internal.net.VpnConfig");
    public static RefObject<String> user;
}
