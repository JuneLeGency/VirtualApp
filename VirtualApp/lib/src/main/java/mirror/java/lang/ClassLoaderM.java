package mirror.java.lang;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;

/**
 * @author legency
 * @date 2018/05/03.
 */
public class ClassLoaderM {

    public static java.lang.Class<?> TYPE = RefClass.load(ClassLoaderM.class, "java.lang.ClassLoader");

    @MethodParams({String.class, boolean.class})
    public static RefMethod<Class<?>> loadClass;
}
