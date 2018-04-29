package mirror.android.dex;

import mirror.RefClass;
import mirror.RefMethod;

/**
 * @author legency
 * @date 2018/04/28.
 */
public class DexM {
    public static java.lang.Class<?> TYPE = RefClass.load(DexM.class, "com.android.dex.Dex");
    public static RefMethod<byte []> getBytes;
}
