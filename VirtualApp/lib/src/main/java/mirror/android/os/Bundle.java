package mirror.android.os;

import android.os.IBinder;

import java.util.Map;

import mirror.RefClass;
import mirror.RefMethod;
import mirror.MethodParams;
import mirror.RefObject;

/**
 * @author Lody
 */

public class Bundle {
    public static Class<?> TYPE = RefClass.load(Bundle.class, android.os.Bundle.class);

    @MethodParams({String.class, IBinder.class})
    public static RefMethod<Void> putIBinder;

    @MethodParams({String.class})
    public static RefMethod<IBinder> getIBinder;
    public static class BaseBundle{

        public static Class<?> TYPE = RefClass.load(BaseBundle.class, android.os.BaseBundle.class);

        public static RefObject<Map<String,Object>> mMap;

        public static RefMethod<Void> unparcel;
    }


}
