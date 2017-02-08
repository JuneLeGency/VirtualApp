package june.legency.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.lc.puppet.storage.IObIndex;

import java.util.HashMap;

import june.legency.env.R;
import june.legency.env.bridge.EnvManager;
import june.legency.env.fragments.AMapFragment;

public class EnvDetailActivity extends AppCompatActivity {

    private String env;
    private HashMap<IObIndex, Object> result = new HashMap<>();
    private AMap mAMap;
    private MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle e = getIntent().getExtras();
        env = e.getString("env");
        setTitle(env);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        result.clear();
        EnvManager.get(this).obtainDbIntoHash(env, result, null);
        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mAMap = mapView.getMap();
        AMapFragment.setMapCustomStyleFile(this,mAMap);
        render();
    }

    private void render() {
        Location location = (Location) result.get(IObIndex.LOCATION);
        if (location == null) return;
        LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
        CoordinateConverter converter  = new CoordinateConverter(this);
// CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标点 LatLng类型
        converter.coord(l);
// 执行转换操作
        try {
            l = converter.convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (marker == null)
            marker = new MarkerOptions().position(l);
        else
            marker.position(l);
        mAMap.addMarker(marker);
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(new CameraPosition(l, 18, 0, 0));
        mAMap.moveCamera(c);
    }

}
