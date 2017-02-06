package june.legency.env.bridge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lc.puppet.client.local.interceptor.VInterceptorCallManager;
import com.lc.puppet.storage.IObIndex;

import java.util.HashMap;

/**
 * Created by lichen:) on 2016/9/12.
 */
public class EnvManager {

    private static EnvManager instance;

    private boolean locationSucceed;

    private ObjectTransfer objectTransfer;

    public EnvManager(Context context) {
        this.context = context;
        initService();
    }

    public static synchronized EnvManager get(Context context) {
        if (instance != null) return instance;
        synchronized (EnvManager.class) {
            return instance = new EnvManager(context);
        }
    }

    Context context;

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private LocationManager locationManager;

    void initService() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    private Object getSystemService(String service) {
        return context.getSystemService(service);
    }

    public void saveCurrentEnv() {
        save(IObIndex.NETWORK_INFO, connectivityManager.getActiveNetworkInfo());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            save(IObIndex.CELL_INFOS, telephonyManager.getAllCellInfo());
        }
        save(IObIndex.CELL_LOCATION, telephonyManager.getCellLocation());
        save(IObIndex.NEIGHBORING_CELL_INFOS, telephonyManager.getNeighboringCellInfo());
        save(IObIndex.PHONE_TYPE, telephonyManager.getPhoneType());
        save(IObIndex.WIFI_INFO, wifiManager.getConnectionInfo());
        save(IObIndex.SCAN_RESULTS, wifiManager.getScanResults());
        save(IObIndex.WIFI_STATE, wifiManager.getWifiState());

        locationSucceed = false;
        saveLocation(LocationManager.GPS_PROVIDER);
        saveLocation(LocationManager.NETWORK_PROVIDER);
    }

    public void saveLocation(String provider) {
        if (locationSucceed) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }

        locationManager.requestLocationUpdates(provider,
                0,
                0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        save(IObIndex.LOCATION, location);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                        }
                        locationManager.removeUpdates(this);
                        locationSucceed = true;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }

    void save(IObIndex key, Object o) {
        VInterceptorCallManager.get().save(key, o);
    }

    HashMap<IObIndex, Object> result;

    public void obtainEnvIntoHash(HashMap<IObIndex, Object> result) {
        obtainEnvIntoHash(result, null);
    }

    public void obtainEnvIntoHash(HashMap<IObIndex, Object> result, ObjectTransfer objectTransfer) {
        if (result == null || !result.isEmpty()) {
            Log.e("EnvManager", "obtainEnvIntoHash your hash has error");
            return;
        }
        this.result = result;
        this.objectTransfer = objectTransfer;
        store(IObIndex.NETWORK_INFO, connectivityManager.getActiveNetworkInfo());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            store(IObIndex.CELL_INFOS, telephonyManager.getAllCellInfo());
        }
        store(IObIndex.CELL_LOCATION, telephonyManager.getCellLocation());
        store(IObIndex.NEIGHBORING_CELL_INFOS, telephonyManager.getNeighboringCellInfo());
        store(IObIndex.WIFI_INFO, wifiManager.getConnectionInfo());
        store(IObIndex.SCAN_RESULTS, wifiManager.getScanResults());
        store(IObIndex.WIFI_STATE, wifiManager.getWifiState());
        store(IObIndex.PHONE_TYPE, telephonyManager.getPhoneType());
    }

    void store(IObIndex key, Object o) {
        Object obt = o;
        if (objectTransfer != null) {
            obt = objectTransfer.transfer(o);
        }
        result.put(key, obt);
    }

    public void showLocation(String provider, LocationListener l) {
        if (locationSucceed) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(provider,
                0,
                0, l
        );
    }

    public void remove(LocationListener locationListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.removeUpdates(locationListener);
    }

    public void backup() {
        VInterceptorCallManager.get().backup();
    }

    public void recover() {
        VInterceptorCallManager.get().recover();
    }
}
