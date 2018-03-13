package june.legency.env.activities;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.GsonBuilder;
import com.lc.puppet.client.local.interceptor.VInterceptorCallManager;
import com.lc.puppet.storage.IObIndex;
import com.lody.virtual.client.core.VirtualCore;

import java.util.HashMap;
import java.util.List;

import june.legency.env.R;
import june.legency.env.ShowListAdapter;
import june.legency.env.bridge.EnvManager;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Switch interceptor_switch;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private List<String> menus;
    private TextView menuName;
    private HashMap<IObIndex, Object> result;
    private ListView mShowList;
    private ShowListAdapter mAdapter;

    private AdapterView.OnItemClickListener showClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ShowListAdapter.KV item = mAdapter.getItem(position);
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this,DetailActivity.class);
            intent.putExtra("key",item.getKey().toString());
            intent.putExtra("value",new GsonBuilder().setPrettyPrinting().create().toJson(item.getValue()));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        interceptor_switch = (Switch) findViewById(R.id.interceptor_switch);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        interceptor_switch.setOnCheckedChangeListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.add_menu).setOnClickListener(this);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.recover).setOnClickListener(this);
        findViewById(R.id.backup).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        mShowList = (ListView) findViewById(R.id.show_list);
        mAdapter = new ShowListAdapter(this);
        mShowList.setAdapter(mAdapter);
        mShowList.setOnItemClickListener(showClicked);
        menuName = (TextView) findViewById(R.id.menu);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMenus();
    }

    private void getMenus() {
        try {
            menus = VInterceptorCallManager.get().getInterface().getEnvs();
            String current = VInterceptorCallManager.get().getInterface().getCurrentEnv();
            int position = menus.indexOf(current);
            adapter.clear();
            adapter.addAll(menus);
            adapter.notifyDataSetChanged();
            spinner.setSelection(position >= 0 ? position : 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void init() {
        interceptor_switch.setChecked(VirtualCore.get().isInterceptorEnabled());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.interceptor_switch) {
            try {
                VInterceptorCallManager.get().getInterface().setObFlowEnable(isChecked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.save) {
            EnvManager.get(this).saveCurrentEnv();

        } else if (i == R.id.add_menu) {
            try {
                if (TextUtils.isEmpty(menuName.getText()))
                    return;
                VInterceptorCallManager.get().getInterface().addEnv(menuName.getText().toString());
                menuName.setText("");
                getMenus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (i == R.id.show) {
            show();
        } else if (i == R.id.backup) {
            backup();
        } else if (i == R.id.recover) {
            recover();
        } else if (i == R.id.delete) {
            delete();
        }
    }

    private void delete() {
        try {
            VInterceptorCallManager.get().getInterface().delEnv((String) spinner.getSelectedItem());
            getMenus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void recover() {
        EnvManager.get(this).recover();
        getMenus();
    }

    private void backup() {
        EnvManager.get(this).backup();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                LogUtils.d("SettingActivitylocation getted");
                result.put(IObIndex.LOCATION, location);
                changes();
                EnvManager.get(SettingActivity.this).remove(this);
            }
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
    };

    private void changes() {

        mAdapter.bindHash(result);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * show current env
     */
    private void show() {
        if (result == null) {
            result = new HashMap<>();
        }
        result.clear();
        EnvManager.get(this).obtainEnvIntoHash(this.result);
        changes();
        EnvManager.get(this).showLocation(LocationManager.GPS_PROVIDER, locationListener);
        EnvManager.get(this).showLocation(LocationManager.NETWORK_PROVIDER, locationListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            VInterceptorCallManager.get().getInterface().setCurrentEnv(menus.get(position));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
