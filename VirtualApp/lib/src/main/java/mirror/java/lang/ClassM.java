package mirror.java.lang;

import mirror.RefClass;
import mirror.RefObject;

/**
 * @author legency
 * @date 2018/04/28.
 */
public class ClassM {
    public static java.lang.Class<?> TYPE = RefClass.load(ClassM.class, "java.lang.Class");
    public static RefObject<Object> dexCache;


}
