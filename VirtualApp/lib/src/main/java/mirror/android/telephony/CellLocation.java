package mirror.android.telephony;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;
import mirror.android.os.Bundle;

/**
 * Created by legency on 2017/2/6.
 */

public class CellLocation {
    public static Class<?> TYPE = RefClass.load(CellLocation.class, "android.telephony.CellLocation");
    @MethodParams(Bundle.class)
    public static RefStaticMethod<android.telephony.CellLocation> newFromBundle;
}
