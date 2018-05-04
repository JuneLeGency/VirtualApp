package mirror.java.lang;

import mirror.RefClass;
import mirror.RefMethod;
import mirror.RefObject;

/**
 * @author legency
 * @date 2018/04/28.
 */
public class DexCacheM {
    public static java.lang.Class<?> TYPE = RefClass.load(DexCacheM.class, "java.lang.DexCache");
    public static RefMethod<Object> getDex;
    public static RefObject<String> location;
}