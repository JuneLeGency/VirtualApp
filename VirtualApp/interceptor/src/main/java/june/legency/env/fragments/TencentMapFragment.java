package june.legency.env.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lody.virtual.helper.utils.VLog;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.CameraPosition;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.UiSettings;
import june.legency.env.R;

/**
 * @author legency
 * @date 2018/03/12.
 */

public class TencentMapFragment extends Fragment implements TencentLocationListener {

    private MapView mMapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        UiSettings uiSettings = mMapView.getUiSettings();
        //设置logo到屏幕底部中心
        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM);
        //设置比例尺到屏幕右下角
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        //启用缩放手势(更多的手势控制请参考开发手册)
        uiSettings.setZoomGesturesEnabled(true);
        startLocation();
    }

    private void startLocation() {
        TencentLocationRequest request = TencentLocationRequest.create()
            .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO)
            .setAllowGPS(true);
        int error = TencentLocationManager.getInstance(getContext())
            .requestLocationUpdates(request, this);
        if (error != 0) {
            VLog.w("TMap", "startLocation:error=" + error);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tencent_map, null);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (tencentLocation != null) {
            TencentLocationManager.getInstance(getContext()).removeUpdates(this);
            locationChanges(tencentLocation);
        } else {
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    void locationChanges(TencentLocation tencentLocation){
        LatLng lt = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
        MarkerOptions markerOption = new MarkerOptions()
            .icon(BitmapDescriptorFactory.defaultMarker())
            .position(lt)
            .draggable(true);
        mMapView.clearAllOverlays();
        mMapView.addMarker(markerOption);
        mMapView.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lt, 18)));
    }
}
